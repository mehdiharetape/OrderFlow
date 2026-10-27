package com.example.order_flow.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.sql.In;

import java.math.BigDecimal;

@Table("order_item")
public class OrderItem {
    @Id
    private Integer orderItemId;
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    private BigDecimal unitPrice;

    public OrderItem(){}

    public OrderItem(Integer orderItemId){
        this.orderItemId = orderItemId;
    }

    //getters
    public Integer getOrderItemId() {
        return orderItemId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    //setters
    public void setOrderItemId(Integer orderItemId){
        this.orderItemId = orderItemId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
