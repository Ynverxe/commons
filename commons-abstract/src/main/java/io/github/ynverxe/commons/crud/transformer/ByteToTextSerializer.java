package io.github.ynverxe.commons.crud.transformer;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ByteToTextSerializer implements DirectionalTransformer<byte[], String> {

  private final Charset charset;

  public ByteToTextSerializer(@NotNull Charset charset) {
    this.charset = Objects.requireNonNull(charset);
  }

  public ByteToTextSerializer() {
    this(StandardCharsets.UTF_8);
  }

  @Override
  public byte @NotNull [] transformRight(@NotNull String right) {
    return right.getBytes(this.charset);
  }

  @Override
  public @NotNull String transformLeft(byte @NotNull [] left) {
    return new String(left, this.charset);
  }
}