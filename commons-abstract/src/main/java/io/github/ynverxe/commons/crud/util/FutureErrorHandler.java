package io.github.ynverxe.commons.crud.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApiStatus.Internal
public final class FutureErrorHandler {

  public static <T> @NotNull Function<Throwable, T> log(@NotNull Logger logger, @NotNull String message) {
    return throwable -> {
      logger.log(Level.SEVERE, throwable, () -> message);
      return null;
    };
  }

  public static <T> @NotNull Function<Throwable, T> chooseLoggerImplAndLog(@NotNull Class<?> caller, @NotNull String message) {
    if (SLF4JHelper.IS_API_PRESENT) {
      return log(Logger.getLogger(caller.getSimpleName()), message);
    }

    return throwable -> {
      SLF4JHelper.logError(caller, throwable, message);
      return null;
    };
  }
}