package com.example.order_flow.dto.refund;

import java.math.BigDecimal;

public class CreateRefundDto {
    private Integer paymentId;
    private BigDecimal amount;

    //getters

    public Integer getPaymentId() {
        return paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    //setters

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
