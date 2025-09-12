package io.github.ynverxe.commons.configuration.model.parsing;

import io.github.ynverxe.commons.configuration.model.parsing.exception.InLineModelParsingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;

public class InLineModelParser<T> {

  private final List<Expect<?>> expectList;
  private final @NotNull Function<ArgumentIterator, T> constructor;
  private final boolean ignoreTrailingData;
  private final char separator;
  private final boolean strict;

  public InLineModelParser(@NotNull List<Expect<?>> expectList, @NotNull Function<ArgumentIterator, T> constructor, boolean ignoreTrailingData, char separator, boolean strict) {
    this.expectList = Objects.requireNonNull(expectList);
    this.constructor = Objects.requireNonNull(constructor);
    this.separator = separator;
    this.ignoreTrailingData = ignoreTrailingData;
    this.strict = strict;
  }

  public static <T> @NotNull Builder<T> newBuilder() {
    return new Builder<>();
  }

  public @NotNull T parse(@NotNull String line) throws InLineModelParsingException {
    List<Object> parsedArguments = new ArrayList<>();

    Iterator<Expect<?>> expectIterator = this.expectList.iterator();
    List<String> arguments = collectArguments(line);

    for (int index = 0; index < arguments.size(); index++) {
      String argument = arguments.get(index);

      if (!expectIterator.hasNext() && !ignoreTrailingData)
        throw new InLineModelParsingException("There's trailing data but not expects to parse");

      Expect<?> expect = expectIterator.next();

      if (argument.isBlank()) {
        parsedArguments.add(null);
        continue;
      }

      Object parsed = performParse(argument, expect);

      if (parsed == null && !expect.optional()) {
        throw new InLineModelParsingException("Cannot parse argument at position '" + (index + 1) + "' into '" + expect.name() + "'");
      }

      parsedArguments.add(parsed);
    }

    ArgumentIterator context = new ArgumentIterator(parsedArguments);
    return this.constructor.apply(context);
  }

  public TypeSerializer<T> asTypeDeserializer() {
    return new InLineModelTypeDeserializer<>(this);
  }

  private List<String> collectArguments(@NotNull String line) {
    if (line.contains("\n"))
      throw new InLineModelParsingException("newline characters are not allowed");

    List<Character> characters = new ArrayList<>(line.length());
    for (char character : line.toCharArray()) {
      characters.add(character);
    }

    final char escapingChar = '\\';
    final char quoteDelimiter = '\'';
    final char doubleQuoteDelimiter = '\"';

    List<String> arguments = new ArrayList<>();
    StringBuilder argumentBuilder = new StringBuilder();

    ListIterator<Character> iterator = characters.listIterator();
    while (iterator.hasNext()) {
      char character = iterator.next();

      if (character == quoteDelimiter || character == doubleQuoteDelimiter) {
        consumeUntil(argumentBuilder, iterator, character);
        continue;
      }

      if (character == escapingChar && iterator.hasNext()) {
        char next = iterator.next();

        if (next != separator) {
          iterator.previous();
        } else {
          argumentBuilder.append(next);
          continue;
        }
      }

      if (character != separator) {
        argumentBuilder.append(character);
        continue;
      }

      String fullArgument = argumentBuilder.toString();
      arguments.add(fullArgument);
      argumentBuilder = new StringBuilder();
    }

    if (!argumentBuilder.isEmpty()) {
      arguments.add(argumentBuilder.toString());
    }

    return arguments;
  }

  private void consumeUntil(StringBuilder builder, ListIterator<Character> characterIterator, char delimiter) {
    while (characterIterator.hasNext()) {
      char next = characterIterator.next();

      if (next != delimiter) {
        builder.append(next);
        continue;
      }

      return;
    }

    if (!strict)
      return;

    throw new InLineModelParsingException("Expected delimiter '" + delimiter + "' but reached End of Line");
  }

  public static class Builder<T> {
    private final List<Expect<?>> expectList = new ArrayList<>();
    private Function<ArgumentIterator, T> constructor;
    private boolean ignoreTrailingData;
    private char separator = ':';
    private boolean strict;

    public boolean strict() {
      return strict;
    }

    public Builder<T> strict(boolean strict) {
      this.strict = strict;
      return this;
    }

    public char separator() {
      return separator;
    }

    public Builder<T> separator(char separator) {
      this.separator = separator;
      return this;
    }

    public Builder<T> ignoreTrailingData(boolean ignoreTrailingData) {
      this.ignoreTrailingData = ignoreTrailingData;
      return this;
    }

    public Builder<T> constructor(@NotNull Function<ArgumentIterator, T> constructor) {
      this.constructor = Objects.requireNonNull(constructor);
      return this;
    }

    public @NotNull Builder<T> expect(@NotNull Expect<?> expect) {
      this.expectList.add(expect);
      return this;
    }

    public InLineModelParser<T> build() {
      if (this.expectList.isEmpty()) {
        throw new IllegalStateException("No expects defined");
      }

      return new InLineModelParser<>(this.expectList, Objects.requireNonNull(this.constructor, "constructor is not assigned"), this.ignoreTrailingData, this.separator, this.strict);
    }
  }

  static <E> @Nullable E performParse(String argument, Expect<E> expect) {
    Matcher matcher = expect.pattern().matcher(argument);

    if (!matcher.matches())
      return null;

    return expect.argumentParser().parse(matcher);
  }
}