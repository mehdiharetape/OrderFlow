package com.example.order_flow.dto.payment;

public class CompleteOnlineRefundDto {
    private Integer refundId;
    private boolean success;
    private String refundReference;

    //getters

    public Integer getRefundId(){
        return refundId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getRefundReference() {
        return refundReference;
    }

    //setters
    public void setRefundId(Integer refundId){
        this.refundId = refundId;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setRefundReference(String refundReference) {
        this.refundReference = refundReference;
    }
}
