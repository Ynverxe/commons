package io.github.ynverxe.commons.crud;

import io.github.ynverxe.commons.crud.error.AsyncErrorLogger;
import io.github.ynverxe.commons.crud.error.AsyncHandler;
import io.github.ynverxe.commons.crud.error.MissingEntityStrategy;
import io.github.ynverxe.commons.crud.error.impl.AbstractErrorLogger;
import io.github.ynverxe.commons.crud.exception.AsyncModelRepositoryException;
import io.github.ynverxe.commons.crud.exception.EntityNotFoundException;
import io.github.ynverxe.commons.crud.exception.FailedCrudOperationException;
import io.github.ynverxe.commons.crud.exception.ObjectMappingException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DefaultModelRepository<K, V, R> implements ModelRepository<K, V, R> {

  private final @NotNull CrudRepository<K, R> crudRepository;
  private final @NotNull ModelSerializer<R, K, V> modelSerializer;
  private final @NotNull MissingEntityStrategy<K, V> missingEntityStrategy;

  public DefaultModelRepository(@NotNull CrudRepository<K, R> crudRepository, @NotNull ModelSerializer<R, K, V> modelSerializer, @NotNull MissingEntityStrategy<K, V> missingEntityStrategy) {
    this.crudRepository = Objects.requireNonNull(crudRepository);
    this.modelSerializer = Objects.requireNonNull(modelSerializer);
    this.missingEntityStrategy = Objects.requireNonNull(missingEntityStrategy);
  }

  public @NotNull R saveModel(@NotNull K key, @NotNull V object) throws ObjectMappingException, FailedCrudOperationException {
    R rawData = this.modelSerializer.serialize(key, object);
    this.crudRepository.upsert(key, rawData);
    return rawData;
  }

  public void deleteModel(@NotNull K key) throws FailedCrudOperationException {
    this.crudRepository.remove(key);
  }

  public @NotNull V getModel(@NotNull K key) throws ObjectMappingException, FailedCrudOperationException {
    try {
      R rawData = this.crudRepository.read(key);
      return this.modelSerializer.deserialize(key, rawData);
    } catch (EntityNotFoundException exception) {
      V fallback = this.missingEntityStrategy.createDefault(key);

      if (fallback != null) {
        return fallback;
      }

      throw exception;
    }
  }

  public static class Async<K, V, R> extends DefaultModelRepository<K, V, R> implements ModelRepository.Async<K, V, R> {

    private final @NotNull Executor executor;
    private final @NotNull AsyncErrorLogger errorLogger;

    public Async(
        @NotNull CrudRepository<K, R> crudRepository,
        @NotNull ModelSerializer<R, K, V> modelSerializer,
        @NotNull MissingEntityStrategy<K, V> missingEntityStrategy,
        @NotNull AsyncErrorLogger errorLogger,
        @NotNull Executor executor
    ) {
      super(crudRepository, modelSerializer, missingEntityStrategy);
      this.executor = Objects.requireNonNull(executor);
      this.errorLogger = Objects.requireNonNull(errorLogger);

      if (errorLogger instanceof AbstractErrorLogger<?> abstractErrorLogger) {
        abstractErrorLogger.setHolder(getClass());
      }
    }

    public Async(
        @NotNull CrudRepository<K, R> crudRepository,
        @NotNull ModelSerializer<R, K, V> modelSerializer,
        @NotNull AsyncHandler<K, V> asyncHandler,
        @NotNull Executor executor
    ) {
      this(crudRepository, modelSerializer, asyncHandler, asyncHandler, executor);
    }

    @Override
    public @NotNull <T> CompletableFuture<T> supply(@NotNull Callable<T> callable) {
      return CompletableFuture.supplyAsync(() -> {
            try {
              return callable.call();
            } catch (Exception e) {
              throw new AsyncModelRepositoryException(e);
            }
          }, this.executor)
          .exceptionally(this.errorLogger.function());
    }

    @Override
    public @NotNull CompletableFuture<Void> run(@NotNull Callable<Void> callable) {
      return CompletableFuture.runAsync(() -> {
            try {
              callable.call();
            } catch (Exception e) {
              throw new AsyncModelRepositoryException(e);
            }
          }, this.executor)
          .exceptionally(this.errorLogger.function());
    }
  }
}