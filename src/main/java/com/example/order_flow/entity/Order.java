package com.example.order_flow.entity;

import com.example.order_flow.entity.enums.OrderStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("order")
public class Order {
    @Id
    private Integer orderId;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private BigDecimal totalAmount;

    public Order(){}

    public Order(Integer orderId){
        this.orderId = orderId;
    }

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
    public void setOrderId(Integer orderId){this.orderId = orderId;}

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
