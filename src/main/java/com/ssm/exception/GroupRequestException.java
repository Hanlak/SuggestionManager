package com.ssm.exception;

public class GroupRequestException extends Exception{
    protected GroupRequestException() {}

    public GroupRequestException(String message) {
        super(message);
    }
}
