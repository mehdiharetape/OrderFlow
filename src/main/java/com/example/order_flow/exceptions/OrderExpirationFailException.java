package com.example.order_flow.exceptions;

public class OrderExpirationFailException extends RuntimeException{
    public OrderExpirationFailException(String message)
    {
        super(message);
    }
}
