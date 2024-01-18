package com.ssm.exception;

public class PendingRequestException extends Exception{
    protected PendingRequestException() {}

    public PendingRequestException(String message) {
        super(message);
    }
}
