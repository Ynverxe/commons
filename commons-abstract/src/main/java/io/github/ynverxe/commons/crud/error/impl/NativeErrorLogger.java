package io.github.ynverxe.commons.crud.error.impl;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NativeErrorLogger extends AbstractErrorLogger<Logger> {

  @Override
  protected void doLog(@NotNull Logger logger, @NotNull Throwable throwable) {
    logger.log(Level.SEVERE, this.message, throwable);
  }

  @Override
  protected Logger createLogger(@NotNull Class<?> holder) {
    return Logger.getLogger(holder.getSimpleName());
  }
}