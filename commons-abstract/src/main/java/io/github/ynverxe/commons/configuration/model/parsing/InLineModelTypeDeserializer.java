package io.github.ynverxe.commons.configuration.model.parsing;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

public class InLineModelTypeDeserializer<T> implements TypeSerializer<T> {

  private final InLineModelParser<T> parser;

  public InLineModelTypeDeserializer(@NotNull InLineModelParser<T> parser) {
    this.parser = Objects.requireNonNull(parser);
  }

  @Override
  public T deserialize(@NotNull Type type, @NotNull ConfigurationNode node) throws SerializationException {
    if (!(node.rawScalar() instanceof String string))
      return null;

    return this.parser.parse(string);
  }

  @Override
  public void serialize(@NotNull Type type, @Nullable T obj, @NotNull ConfigurationNode node) throws SerializationException {
    throw new UnsupportedOperationException();
  }
}