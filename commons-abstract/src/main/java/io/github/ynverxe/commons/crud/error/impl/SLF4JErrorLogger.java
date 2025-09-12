package io.github.ynverxe.commons.crud.error.impl;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class SLF4JErrorLogger extends AbstractErrorLogger {

  public SLF4JErrorLogger(@NotNull String message) {
    super(message);
  }

  public SLF4JErrorLogger() {
  }

  @Override
  protected void doLog(@NotNull Object logger, @NotNull Throwable throwable) {
    ((Logger) logger).error(this.message, throwable);
  }

  @Override
  protected Object createLogger(@NotNull Class holder) {
    return LoggerFactory.getLogger(holder);
  }
}