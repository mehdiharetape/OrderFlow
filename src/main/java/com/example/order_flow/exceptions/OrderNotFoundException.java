package com.example.order_flow.exceptions;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(Integer orderId){
        super("Order '" + orderId + "' not found!");
    }
}
