package com.example.order_flow.exceptions;

public class UpdateProductFailException extends RuntimeException{
    public UpdateProductFailException(String message){
        super(message);
    }
}
