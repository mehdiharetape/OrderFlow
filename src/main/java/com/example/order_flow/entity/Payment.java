package com.example.order_flow.entity;

import com.example.order_flow.entity.enums.PaymentStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("payment")
public class Payment {
    @Id
    private Integer paymentId;
    private Integer orderId;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private String transactionReference;

    public Payment(){}

    public Payment(Integer paymentId){
        this.paymentId = paymentId;
    }

    //getters
    public Integer getPaymentId() {
        return paymentId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public String getTransactionReference(){
        return transactionReference;
    }

    //setters

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public void setTransactionReference(String transactionReference){
        this.transactionReference = transactionReference;
    }
}
