package com.example.order_flow.dto.payment;

public class CompleteOnlinePaymentDto {
    private Integer paymentId;
    private boolean success;
    private String transactionalReference;

    public Integer getPaymentId(){
        return paymentId;
    }

    public boolean getSuccess(){
        return success;
    }

    public String getTransactionalReference(){
        return transactionalReference;
    }

    public void setPaymentId(Integer paymentId){
        this.paymentId = paymentId;
    }

    public void setSuccess(boolean success){
        this.success = success;
    }

    public void setTransactionalReference(String transactionalReference){
        this.transactionalReference = transactionalReference;
    }
}
