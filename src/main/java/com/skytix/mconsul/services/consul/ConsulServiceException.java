package com.skytix.mconsul.services.consul;

public class ConsulServiceException extends RuntimeException {

    public ConsulServiceException() {
    }

    public ConsulServiceException(String message) {
        super(message);
    }

    public ConsulServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsulServiceException(Throwable cause) {
        super(cause);
    }

    public ConsulServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
