package com.ssm.exception;

public class PaymentException extends Exception{
    protected PaymentException() {}

    public PaymentException(String message) {
        super(message);
    }
}
