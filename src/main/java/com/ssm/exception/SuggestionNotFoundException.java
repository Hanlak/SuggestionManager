package com.ssm.exception;

public class SuggestionNotFoundException extends Exception{
    protected SuggestionNotFoundException() {}

    public SuggestionNotFoundException(String message) {
        super(message);
    }
}
