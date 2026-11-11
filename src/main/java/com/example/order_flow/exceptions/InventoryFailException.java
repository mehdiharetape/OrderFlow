package com.example.order_flow.exceptions;

public class InventoryFailException extends RuntimeException{
    public InventoryFailException(String message){
        super(message);
    }
}
