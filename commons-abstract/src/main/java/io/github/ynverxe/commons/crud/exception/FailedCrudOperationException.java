package io.github.ynverxe.commons.crud.exception;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FailedCrudOperationException extends Exception {

  public FailedCrudOperationException(String message, @NotNull Throwable cause) {
    super(message, Objects.requireNonNull(cause));
  }

  public FailedCrudOperationException(@NotNull Throwable cause) {
    super(Objects.requireNonNull(cause));
  }

  public FailedCrudOperationException(String message, @NotNull Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, Objects.requireNonNull(cause), enableSuppression, writableStackTrace);
  }

  @Override
  public synchronized @NotNull Throwable getCause() {
    return super.getCause();
  }
}