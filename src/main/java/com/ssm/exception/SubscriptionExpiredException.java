package com.ssm.exception;

public class SubscriptionExpiredException extends Exception{
    protected SubscriptionExpiredException() {}

    public SubscriptionExpiredException(String message) {
        super(message);
    }
}
