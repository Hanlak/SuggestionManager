package com.ssm.exception;

public class UserAlreadyExistsException extends Exception{
    protected UserAlreadyExistsException() {}

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
