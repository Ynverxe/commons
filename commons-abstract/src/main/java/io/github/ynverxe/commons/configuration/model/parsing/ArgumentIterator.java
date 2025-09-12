package io.github.ynverxe.commons.configuration.model.parsing;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ArgumentIterator {

  private final List<Object> objects;
  private int index = -1;

  public ArgumentIterator(List<Object> objects) {
    this.objects = Objects.requireNonNull(objects);
  }

  /**
   * Attempts to move the cursor one position ahead and return the
   * found element and cast it to the  <code>expectedType</code> if the end wasn't reached, otherwise it'll return null.
   */
  public <T> T next(@NotNull Class<T> expectedType) {
    if (!hasNext())
      return null;

    return expectedType.cast(this.objects.get(++index));
  }

  /**
   * Attempts to move the cursor one position ahead and return the
   * found element and cast it to the <code>expectedType</code> if the end wasn't reached, otherwise it'll return the default value.
   */
  public <T> @NotNull T nextOr(@NotNull Class<T> expectedType, @NotNull T def) {
    Objects.requireNonNull(def, "default value is null");

    if (!hasNext())
      return def;

    Object next = this.objects.get(++this.index);

    if (!expectedType.isInstance(next)) {
      return def;
    }

    return expectedType.cast(next);
  }

  public int count() {
    return this.objects.size();
  }

  public int remaining() {
    return (this.index + 1) - count();
  }

  public boolean hasNext() {
    return this.index + 1 < this.objects.size();
  }
}
