package org.yaz4j.exception;

public class ConnectionUnavailableException extends ZoomException {

  private static final long serialVersionUID = 1L;

  public ConnectionUnavailableException() {
    super();
  }

  public ConnectionUnavailableException(String message) {
    super(message);
  }
}
