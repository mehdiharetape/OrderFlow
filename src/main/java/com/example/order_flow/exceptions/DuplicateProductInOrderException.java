package com.example.order_flow.exceptions;

public class DuplicateProductInOrderException extends RuntimeException{
    public DuplicateProductInOrderException(Integer productId)
    {
        super("Product " + productId + " is duplicated in this order!");
    }
}
