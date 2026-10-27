package com.example.order_flow.exceptions;

public class InactiveProductException extends RuntimeException{
    public InactiveProductException(Integer productId) {
        super("Product " + productId + " is INACTIVE");
    }
}
