package io.github.ynverxe.commons.configuration.model.parsing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;

public record EnumParser<T extends Enum<T>>(@NotNull Class<T> enumClass) implements ArgumentParser<T> {
  @Override
  public @Nullable T parse(@NotNull Matcher matcher) {
    try {
      return Enum.valueOf(this.enumClass, matcher.group().toUpperCase());
    } catch (Exception ignored) {
      return null;
    }
  }
}
