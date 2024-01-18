package com.ssm.exception;

public class LeaveGroupException extends Exception{
    protected LeaveGroupException() {}

    public LeaveGroupException(String message) {
        super(message);
    }
}
