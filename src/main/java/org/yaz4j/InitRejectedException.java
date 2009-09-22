package org.yaz4j;

public class InitRejectedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InitRejectedException() {
        super();
    }

    public InitRejectedException(String message) {
        super(message);
    }
}
