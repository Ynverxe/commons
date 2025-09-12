package io.github.ynverxe.commons.crud;

import io.github.ynverxe.commons.crud.exception.ObjectMappingException;
import org.jetbrains.annotations.NotNull;

public interface ModelSerializer<R, I, O> {

  @NotNull O deserialize(@NotNull I id, @NotNull R rawData) throws ObjectMappingException;

  @NotNull R serialize(@NotNull I id, @NotNull O object) throws ObjectMappingException;

  static <R, I, O> @NotNull ModelSerializer<R, I, O> of(@NotNull ExceptionAwareTransformer<R, O> deserializer, @NotNull ExceptionAwareTransformer<O, R> serializer) {
    return new Simple<>(deserializer, serializer);
  }

  record Simple<R, I, O>(@NotNull ExceptionAwareTransformer<R, O> deserializer, @NotNull ExceptionAwareTransformer<O, R> serializer) implements ModelSerializer<R, I, O> {

    @Override
    public @NotNull O deserialize(@NotNull I id, @NotNull R rawData) throws ObjectMappingException {
      try {
        return deserializer.transform(rawData);
      } catch (Throwable e) {
        throw new ObjectMappingException(e);
      }
    }

    @Override
    public @NotNull R serialize(@NotNull I id, @NotNull O object) throws ObjectMappingException {
      try {
        return serializer.transform(object);
      } catch (Throwable e) {
        throw new ObjectMappingException(e);
      }
    }
  }

  interface ExceptionAwareTransformer<T, R> {
    @NotNull R transform(T instance) throws Throwable;
  }
}