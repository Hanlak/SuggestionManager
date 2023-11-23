package com.ssm.exception;

public class EmailSendingException extends Exception{
    protected EmailSendingException() {}

    public EmailSendingException(String message) {
        super(message);
    }
}
