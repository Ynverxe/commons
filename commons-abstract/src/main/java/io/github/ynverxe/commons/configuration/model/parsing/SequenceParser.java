package io.github.ynverxe.commons.configuration.model.parsing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record SequenceParser<E>(@NotNull Expect<E> elementExpect,
                                @NotNull Pattern pattern) implements ArgumentParser<List<E>> {

  public SequenceParser(@NotNull Expect<E> expect) {
    this(expect, Pattern.compile("\\s*([^,\\s][^,]*[^,\\s]?)\\s*(?=,|$)"));
  }

  @Override
  public @Nullable List<E> parse(@NotNull Matcher matcher) {
    if (matcher.groupCount() == 0) {
      return null;
    }

    List<E> elements = new ArrayList<>(matcher.groupCount());

    while (matcher.find()) {
      String matchedElement = matcher.group(1);

      E parsedElement = InLineModelParser.performParse(matchedElement, this.elementExpect);

      if (parsedElement == null) {
        continue;
      }

      elements.add(parsedElement);
    }

    return elements;
  }
}
