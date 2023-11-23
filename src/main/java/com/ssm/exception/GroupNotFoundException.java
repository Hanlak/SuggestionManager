package com.ssm.exception;

public class GroupNotFoundException extends Exception{
    protected GroupNotFoundException() {}

    public GroupNotFoundException(String message) {
        super(message);
    }
}
