package com.ssm.exception;

public class EmailMismatchException extends Exception{
    protected EmailMismatchException() {}

    public EmailMismatchException(String message) {
        super(message);
    }
}
