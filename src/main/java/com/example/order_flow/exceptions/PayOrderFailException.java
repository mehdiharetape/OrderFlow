package com.example.order_flow.exceptions;

public class PayOrderFailException extends RuntimeException{
    public PayOrderFailException(String message){
        super(message);
    }
}
