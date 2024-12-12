package com.sahuid.learnroom.exception;

public class NoAuthException extends RuntimeException{
    public NoAuthException(String message) {
        super(message);
    }
}
