package com.example.order_flow.dto.movement;

import com.example.order_flow.entity.enums.MovementReason;

import java.time.LocalDateTime;

public class CreateMovementDto {
    private Integer inventoryMovementId;
    private Integer orderId;
    private Integer productId;
    private Integer physicalChange;
    private Integer reservedChange;
    private LocalDateTime createdAt;
    private MovementReason reason;

    //getters
    public Integer getInventoryMovementId() {
        return inventoryMovementId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public Integer getPhysicalChange() {
        return physicalChange;
    }

    public Integer getReservedChange() {
        return reservedChange;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public MovementReason getReason() {
        return reason;
    }

    //setters
    public void setInventoryMovementId(Integer inventoryMovementId) {
        this.inventoryMovementId = inventoryMovementId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setPhysicalChange(Integer physicalChange) {
        this.physicalChange = physicalChange;
    }

    public void setReservedChange(Integer reservedChange) {
        this.reservedChange = reservedChange;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setReason(MovementReason reason) {
        this.reason = reason;
    }
}
