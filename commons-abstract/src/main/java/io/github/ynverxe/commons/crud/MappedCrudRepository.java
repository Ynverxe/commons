package io.github.ynverxe.commons.crud;

import io.github.ynverxe.commons.crud.exception.FailedCrudOperationException;
import io.github.ynverxe.commons.crud.exception.TransformationException;
import io.github.ynverxe.commons.crud.transformer.DirectionalTransformer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MappedCrudRepository<T, K, V> implements CrudRepository<K, V> {

  private final CrudRepository<K, T> backing;
  private final DirectionalTransformer<T, V> mapper;

  public MappedCrudRepository(@NotNull CrudRepository<K, T> backing, @NotNull DirectionalTransformer<T, V> mapper) {
    this.backing = Objects.requireNonNull(backing);
    this.mapper = Objects.requireNonNull(mapper);
  }

  @Override
  public void upsert(@NotNull K key, @NotNull V object) throws FailedCrudOperationException {
    T oldData;

    try {
      oldData = this.mapper.transformRight(object);
    } catch (TransformationException exception) {
      throw new FailedCrudOperationException(exception);
    }

    this.backing.upsert(key, oldData);
  }

  @Override
  public void remove(@NotNull K key) throws FailedCrudOperationException {
    this.backing.remove(key);
  }

  @Override
  public @NotNull V read(@NotNull K key) throws FailedCrudOperationException {
    T oldData = this.backing.read(key);
    try {
      return this.mapper.transformLeft(oldData);
    } catch (TransformationException exception) {
      throw new FailedCrudOperationException(exception);
    }
  }
}