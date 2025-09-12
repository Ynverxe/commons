package io.github.ynverxe.commons.configuration.model.parsing;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

public record Expect<T>(Pattern pattern, String name, ArgumentParser<T> argumentParser,
                        boolean optional) {

  public Expect(String regex, String name, ArgumentParser<T> argumentParser) {
    this(regex, name, argumentParser, false);
  }

  public Expect(String regex, String name, ArgumentParser<T> argumentParser, boolean optional) {
    this(Pattern.compile(regex), name, argumentParser, optional);
  }

  public static final @NotNull Expect<?> ANY = new Expect<>("(.+)", "any", ArgumentParser.group());
  public static final @NotNull Expect<Boolean> YES_OR_NO = new Expect<>("(yes|no)", "boolean", ArgumentParser.of("yes"::equalsIgnoreCase));
  public static final @NotNull Expect<Boolean> BOOLEAN = new Expect<>("(true|false)", "boolean", ArgumentParser.of(Boolean::parseBoolean));
  public static final @NotNull Expect<Integer> INT = new Expect<>("(\\d+)", "integer digit", ArgumentParser.of(Integer::parseInt));
  public static final @NotNull Expect<Float> FLOAT = new Expect<>("([-+]?(?:\\d*\\.\\d+|\\d+\\.?\\d*)(?:[eE][-+]?\\d+)?)", "float", ArgumentParser.of(Float::parseFloat));

  public static @NotNull <E> Expect<List<E>> sequence(@NotNull Expect<E> elementExpect) {
    String regex = "\\s*([^,\\s][^,]*[^,\\s]?)\\s*(?=,|$)";
    return new Expect<>(regex, "list of " + elementExpect.name, new SequenceParser<>(elementExpect));
  }

  public static @NotNull <T extends Enum<T>> Expect<T> ofEnum(@NotNull Class<T> enumClass) {
    return ANY.withParser(new EnumParser<>(enumClass));
  }

  @Contract("_ -> new")
  public @NotNull <S> Expect<S> withParser(@NotNull ArgumentParser<S> parser) {
    return new Expect<>(this.pattern, this.name, parser, this.optional);
  }

  @Contract("_ -> new")
  public @NotNull Expect<T> withName(@NotNull String name) {
    return new Expect<>(this.pattern, name, this.argumentParser, this.optional);
  }

  @Contract("-> new")
  public Expect<T> asOptional() {
    return new Expect<>(this.pattern, this.name, this.argumentParser, true);
  }
}
