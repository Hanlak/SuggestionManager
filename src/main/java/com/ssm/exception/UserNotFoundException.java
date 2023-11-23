package com.ssm.exception;

public class UserNotFoundException extends Exception{
    protected UserNotFoundException() {}

    public UserNotFoundException(String message) {
        super(message);
    }
}
