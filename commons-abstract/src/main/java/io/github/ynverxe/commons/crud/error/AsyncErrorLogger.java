package io.github.ynverxe.commons.crud.error;

import io.github.ynverxe.commons.crud.error.impl.NativeErrorLogger;
import io.github.ynverxe.commons.crud.error.impl.SLF4JErrorLogger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface AsyncErrorLogger {
  void logError(@NotNull Throwable throwable);

  @ApiStatus.Internal
  default <T> T logErrorAndReturnNull(@NotNull Throwable throwable) {
    logError(throwable);
    return null;
  }

  default <T> Function<Throwable, T> function() {
    return this::logErrorAndReturnNull;
  }

  static @NotNull AsyncErrorLogger slf4jImpl() {
    return new SLF4JErrorLogger();
  }

  static @NotNull AsyncErrorLogger nativeImpl() {
    return new NativeErrorLogger();
  }
}