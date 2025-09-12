package io.github.ynverxe.commons.configuration.serializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

public record ComponentTypeSerializer(
    @NotNull ComponentSerializer<Component, Component, String> componentSerializer) implements TypeSerializer<Component> {

  public ComponentTypeSerializer(@NotNull ComponentSerializer<Component, Component, String> componentSerializer) {
    this.componentSerializer = Objects.requireNonNull(componentSerializer);
  }

  @Override
  public Component deserialize(Type type, ConfigurationNode node) throws SerializationException {
    if (!(node.raw() instanceof String string)) {
      return null;
    }

    return this.componentSerializer.deserialize(string);
  }

  @Override
  public void serialize(Type type, @Nullable Component obj, ConfigurationNode node) throws SerializationException {
    if (obj == null) {
      obj = Component.empty();
    }

    String serialized = this.componentSerializer.serialize(obj);
    node.set(serialized);
  }

  public static @NotNull ComponentTypeSerializer miniMessage() {
    return new ComponentTypeSerializer(MiniMessage.miniMessage());
  }

  public static @NotNull ComponentTypeSerializer miniMessageNonStrict() {
    return new ComponentTypeSerializer(MiniMessage.builder().strict(false).build());
  }
}