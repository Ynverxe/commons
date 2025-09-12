package io.github.ynverxe.commons.crud.util;

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.LoggerFactory;

@ApiStatus.Internal
public class SLF4JHelper {

  public static final boolean IS_API_PRESENT;

  static {
    boolean present;

    try {
      Class.forName("org.slf4j.LoggerFactory");
      present = true;
    } catch (Exception e) {
      present = false;
    }

    IS_API_PRESENT = present;
  }

  public static void logError(Class<?> clazz, Throwable error, String message) {
    LoggerFactory.getLogger(clazz).error(message, error);
  }
}