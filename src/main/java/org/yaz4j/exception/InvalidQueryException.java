package org.yaz4j.exception;

public class InvalidQueryException extends ZoomException {

  private static final long serialVersionUID = 1L;

  public InvalidQueryException() {
    super();
  }

  public InvalidQueryException(String message) {
    super(message);
  }
}
