package com.example.order_flow.dto.order;

import com.example.order_flow.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderBriefResponseDto {
    private Integer orderId;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private BigDecimal totalAmount;

    //getters
    public Integer getOrderId() {
        return orderId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    //setters

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
