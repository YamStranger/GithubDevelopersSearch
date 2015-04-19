package com.github;

/**
 * User: YamStranger
 * Date: 4/14/15
 * Time: 12:50 PM
 */
public class AccessException extends RuntimeException {
    public AccessException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public AccessException(String message) {
        super(message);
    }
}
