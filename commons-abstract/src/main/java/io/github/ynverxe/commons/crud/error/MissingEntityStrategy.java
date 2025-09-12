package io.github.ynverxe.commons.crud.error;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MissingEntityStrategy<K, T> {
  @Nullable T createDefault(@NotNull K key);

  static <K, T> @NotNull MissingEntityStrategy<K, T> none() {
    return key -> null;
  }

  static <K, T> @NotNull MissingEntityStrategy<K, T> ofValue(@NotNull T fallback) {
    return key -> fallback;
  }
}