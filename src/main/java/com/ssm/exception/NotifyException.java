package com.ssm.exception;

public class NotifyException extends Exception{
    protected NotifyException() {}

    public NotifyException(String message) {
        super(message);
    }
}
