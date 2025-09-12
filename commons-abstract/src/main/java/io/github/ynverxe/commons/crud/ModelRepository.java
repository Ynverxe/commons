package io.github.ynverxe.commons.crud;

import io.github.ynverxe.commons.crud.exception.EntityNotFoundException;
import io.github.ynverxe.commons.crud.exception.FailedCrudOperationException;
import io.github.ynverxe.commons.crud.exception.ObjectMappingException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public interface ModelRepository<K, V, R> {
  @NotNull R saveModel(@NotNull K key, @NotNull V object) throws ObjectMappingException, FailedCrudOperationException;

  void deleteModel(@NotNull K key) throws FailedCrudOperationException;

  @NotNull V getModel(@NotNull K key) throws ObjectMappingException, FailedCrudOperationException;

  default @Nullable V getModelIfPresent(@NotNull K key) throws ObjectMappingException, FailedCrudOperationException {
    try {
      return getModel(key);
    } catch (EntityNotFoundException ignored) {
      return null;
    }
  }

  interface Async<K, V, R> extends ModelRepository<K, V, R> {
    default @NotNull CompletableFuture<R> saveModelAsync(@NotNull K key, @NotNull V model) {
      return supply(() -> this.saveModel(key, model));
    }

    default @NotNull CompletableFuture<Void> deleteModelAsync(@NotNull K key) {
      return run(() -> {
          this.deleteModel(key);
          return null;
      });
    }

    default @NotNull CompletableFuture<@NotNull V> getModelAsync(@NotNull K key) {
      return supply(() -> this.getModel(key));
    }

    default @NotNull CompletableFuture<@Nullable V> getModelAsyncIfPresent(@NotNull K key) {
      return supply(() -> this.getModelIfPresent(key));
    }

    @ApiStatus.Internal
    @NotNull <T> CompletableFuture<T> supply(@NotNull Callable<T> supplier);

    @ApiStatus.Internal
    @NotNull CompletableFuture<Void> run(@NotNull Callable<Void> runnable);
  }
}