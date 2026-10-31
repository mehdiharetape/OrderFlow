package com.example.order_flow.exceptions;

public class OrderCancelFailException extends RuntimeException{
    public OrderCancelFailException(String message){
        super(message);
    }
}
