package com.ssm.exception;

public class EmailAlreadyExistsException extends Exception{
    protected EmailAlreadyExistsException() {}

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
