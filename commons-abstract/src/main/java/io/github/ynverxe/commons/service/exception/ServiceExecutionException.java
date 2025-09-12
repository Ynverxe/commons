package io.github.ynverxe.commons.service.exception;

public class ServiceExecutionException extends RuntimeException {
  public ServiceExecutionException() {
  }

  public ServiceExecutionException(String message) {
    super(message);
  }

  public ServiceExecutionException(String message, Throwable cause) {
    super(message, cause);
  }

  public ServiceExecutionException(Throwable cause) {
    super(cause);
  }

  public ServiceExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}