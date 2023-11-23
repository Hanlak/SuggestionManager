package com.ssm.exception;

public class PasswordUpdateException extends Exception{
    protected PasswordUpdateException() {}

    public PasswordUpdateException(String message) {
        super(message);
    }
}
