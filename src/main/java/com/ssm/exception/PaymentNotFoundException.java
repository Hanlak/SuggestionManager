package com.ssm.exception;

public class PaymentNotFoundException extends Exception{
    protected PaymentNotFoundException() {}

    public PaymentNotFoundException(String message) {
        super(message);
    }
}
