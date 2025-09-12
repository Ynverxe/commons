package io.github.ynverxe.commons.configuration.model.parsing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.regex.Matcher;

public interface ArgumentParser<T> {
  @Nullable T parse(@NotNull Matcher matcher);

  static @NotNull ArgumentParser<String> group() {
    return Matcher::group;
  }

  static <T> @NotNull ArgumentParser<T> of(@NotNull Function<String, T> function) {
    return matcher -> function.apply(matcher.group());
  }
}
