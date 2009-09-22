package org.yaz4j;

public class InvalidQueryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidQueryException() {
        super();
    }

    public InvalidQueryException(String message) {
        super(message);
    }
}
