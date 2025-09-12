package io.github.ynverxe.commons.crud.transformer;

import io.github.ynverxe.commons.crud.exception.TransformationException;
import org.jetbrains.annotations.NotNull;

public interface DirectionalTransformer<L, R> {

  @NotNull L transformRight(@NotNull R right) throws TransformationException;

  @NotNull R transformLeft(@NotNull L left) throws TransformationException;

}