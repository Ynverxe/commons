package io.github.ynverxe.commons.test.configuration.model.parsing;

import io.github.ynverxe.commons.configuration.model.parsing.ArgumentIterator;
import io.github.ynverxe.commons.configuration.model.parsing.Expect;
import io.github.ynverxe.commons.configuration.model.parsing.InLineModelParser;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class InLineParsingTest {

  /**
   * Tests argument separation and argument match using <code>first-argument:second-argument</code> as test subject.
   */
  @Test
  public void testArgumentSeparation() {
    InLineModelParser<ArgumentIterator> modelParser = InLineModelParser.<ArgumentIterator>newBuilder()
        .expect(Expect.ANY)
        .expect(Expect.ANY)
        .constructor(Function.identity())
        .build();

    String line = "first argument:second-argument";
    ArgumentIterator context = modelParser.parse(line);

    assertEquals("first argument", context.next(String.class));
    assertEquals("second-argument", context.next(String.class));
  }

  @Test
  public void testDefaultExpects() {
    InLineModelParser<ArgumentIterator> modelParser = InLineModelParser.<ArgumentIterator>newBuilder()
        .expect(Expect.INT)
        .expect(Expect.FLOAT)
        .expect(Expect.BOOLEAN)
        .expect(Expect.YES_OR_NO)
        .constructor(Function.identity())
        .build();

    String line = "22:.55:true:no";
    ArgumentIterator context = modelParser.parse(line);

    assertEquals(22, context.next(Integer.class));
    assertEquals((float) 0.55, context.next(Float.class));
    assertEquals(true, context.next(Boolean.class));
    assertEquals(false, context.next(Boolean.class));
  }

  @Test
  public void testEscapedArgument() {
    InLineModelParser<ArgumentIterator> modelParser = InLineModelParser.<ArgumentIterator>newBuilder()
        .expect(Expect.ANY)
        .expect(Expect.ANY)
        .constructor(Function.identity())
        .build();

    String line = "'first:argument':\"second:argument\"";
    ArgumentIterator context = modelParser.parse(line);

    assertEquals("first:argument", context.next(String.class));
    assertEquals("second:argument", context.next(String.class));
  }
}