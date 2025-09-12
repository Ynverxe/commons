package io.github.ynverxe.commons.crud;

import io.github.ynverxe.commons.crud.exception.EntityNotFoundException;
import io.github.ynverxe.commons.crud.exception.FailedCrudOperationException;
import io.github.ynverxe.commons.crud.transformer.DirectionalTransformer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CrudRepository<K, V> {
  void upsert(@NotNull K key, @NotNull V object) throws FailedCrudOperationException;

  void remove(@NotNull K key) throws FailedCrudOperationException;

  @NotNull V read(@NotNull K key) throws FailedCrudOperationException;

  default @Nullable V readOrNull(@NotNull K key) throws FailedCrudOperationException {
    try {
      return read(key);
    } catch (EntityNotFoundException ignored) {
      return null;
    }
  }

  default <T> CrudRepository<K, T> newLayer(@NotNull DirectionalTransformer<V, T> transformer) {
    return new MappedCrudRepository<>(this, transformer);
  }
}