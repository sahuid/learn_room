package com.sahuid.learnroom.exception;

public class DataBaseAbsentException extends RuntimeException{

    public DataBaseAbsentException(String message) {
        super(message);
    }
}
