package io.github.ynverxe.commons.crud.error.impl;

import io.github.ynverxe.commons.crud.error.AsyncErrorLogger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AbstractErrorLogger<T> implements AsyncErrorLogger {

  private @Nullable T logger;
  protected final @NotNull String message;

  public AbstractErrorLogger(@NotNull String message) {
    this.message = Objects.requireNonNull(message);
  }

  public AbstractErrorLogger() {
    this("Async error logging");
  }

  @ApiStatus.Internal
  public void setHolder(@NotNull Class<?> holder) {
    this.logger = createLogger(holder);
  }

  @Override
  public void logError(@NotNull Throwable throwable) {
    if (this.logger == null) {
      throwable.printStackTrace();
      return;
    }

    doLog(this.logger, throwable);
  }

  protected abstract T createLogger(@NotNull Class<?> holder);

  protected abstract void doLog(@NotNull T logger, @NotNull Throwable throwable);

}