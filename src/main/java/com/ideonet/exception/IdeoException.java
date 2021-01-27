package com.ideonet.exception;

public class IdeoException extends RuntimeException {

    public IdeoException(String msg) {
        super(msg);
    }

    public IdeoException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
