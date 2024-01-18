package com.ssm.exception;

public class AccessException extends Exception{
    protected AccessException() {}

    public AccessException(String message) {
        super(message);
    }
}
