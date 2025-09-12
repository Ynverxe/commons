package io.github.ynverxe.commons.crud.exception;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AsyncModelRepositoryException extends RuntimeException {

  public AsyncModelRepositoryException(@NotNull Throwable cause) {
    super(Objects.requireNonNull(cause));
  }

  @Override
  public synchronized @NotNull Throwable getCause() {
    return super.getCause();
  }
}