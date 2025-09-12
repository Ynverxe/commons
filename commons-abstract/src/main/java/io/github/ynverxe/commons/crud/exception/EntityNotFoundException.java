package io.github.ynverxe.commons.crud.exception;

import org.jetbrains.annotations.NotNull;

public class EntityNotFoundException extends FailedCrudOperationException {
  public EntityNotFoundException(String message, @NotNull Throwable cause) {
    super(message, cause);
  }

  public EntityNotFoundException(@NotNull Throwable cause) {
    super(cause);
  }

  public EntityNotFoundException(String message, @NotNull Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}