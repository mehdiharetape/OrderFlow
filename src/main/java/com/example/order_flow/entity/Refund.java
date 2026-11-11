package com.example.order_flow.entity;

import com.example.order_flow.entity.enums.RefundStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("refund")
public class Refund {
    @Id
    private Integer refundId;
    private Integer paymentId;
    private BigDecimal amount;
    private RefundStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime refundAt;
    private Integer attemptCount;
    private String refundReference;

    public Refund(){}

    public Refund(Integer refundId){
        this.refundId = refundId;
    }

    //getters

    public Integer getRefundId() {
        return refundId;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getRefundAt() {
        return refundAt;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public String getRefundReference() {
        return refundReference;
    }

    //setters

    public void setRefundId(Integer refundId) {
        this.refundId = refundId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setRefundAt(LocalDateTime refundAt) {
        this.refundAt = refundAt;
    }

    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    public void setRefundReference(String refundReference) {
        this.refundReference = refundReference;
    }
}
