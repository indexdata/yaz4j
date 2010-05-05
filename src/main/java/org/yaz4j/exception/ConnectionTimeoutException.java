package org.yaz4j.exception;

public class ConnectionTimeoutException extends ZoomException {

  private static final long serialVersionUID = 1L;

  public ConnectionTimeoutException() {
    super();
  }

  public ConnectionTimeoutException(String message) {
    super(message);
  }
}
