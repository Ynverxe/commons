package io.github.ynverxe.commons.crud;

import io.github.ynverxe.commons.crud.error.AsyncErrorLogger;
import io.github.ynverxe.commons.crud.error.impl.AbstractErrorLogger;
import io.github.ynverxe.commons.crud.exception.AsyncModelRepositoryException;
import io.github.ynverxe.commons.crud.exception.FailedCrudOperationException;
import io.github.ynverxe.commons.crud.exception.ObjectMappingException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AsyncWrappedModelRepository<K, V, R> implements ModelRepository.Async<K, V, R> {

  private final @NotNull ModelRepository<K, V, R> backing;
  private final @NotNull Executor executor;
  private final @NotNull AsyncErrorLogger asyncErrorLogger;

  public AsyncWrappedModelRepository(@NotNull ModelRepository<K, V, R> backing, @NotNull AsyncErrorLogger errorLogger, @NotNull Executor executor, @NotNull AsyncErrorLogger asyncErrorLogger) {
    this.backing = Objects.requireNonNull(backing);
    this.executor = Objects.requireNonNull(executor);
    this.asyncErrorLogger = Objects.requireNonNull(asyncErrorLogger);
    if (errorLogger instanceof AbstractErrorLogger<?> abstractErrorLogger) {
      abstractErrorLogger.setHolder(getClass());
    }
  }

  @Override
  public @NotNull R saveModel(@NotNull K key, @NotNull V object) throws ObjectMappingException, FailedCrudOperationException {
    return this.backing.saveModel(key, object);
  }

  @Override
  public void deleteModel(@NotNull K key) throws FailedCrudOperationException {
    this.backing.deleteModel(key);
  }

  @Override
  public @NotNull V getModel(@NotNull K key) throws ObjectMappingException, FailedCrudOperationException {
    return this.backing.getModel(key);
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
        .exceptionally(this.asyncErrorLogger.function());
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
        .exceptionally(this.asyncErrorLogger.function());
  }
}