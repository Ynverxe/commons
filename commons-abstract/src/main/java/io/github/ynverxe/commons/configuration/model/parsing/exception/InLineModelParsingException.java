package io.github.ynverxe.commons.configuration.model.parsing.exception;

public class InLineModelParsingException extends RuntimeException {
  public InLineModelParsingException() {
  }

  public InLineModelParsingException(String message) {
    super(message);
  }

  public InLineModelParsingException(String message, Throwable cause) {
    super(message, cause);
  }

  public InLineModelParsingException(Throwable cause) {
    super(cause);
  }

  public InLineModelParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}