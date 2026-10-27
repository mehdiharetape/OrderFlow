package com.example.order_flow.entity;

import com.example.order_flow.entity.enums.MovementReason;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("inventory_movement")
public class InventoryMovement {
    @Id
    private Integer inventoryMovementId;
    private Integer orderId;
    private Integer productId;
    private Integer physicalChange;
    private Integer reservedChange;
    private LocalDateTime createdAt;
    private MovementReason reason;

    public InventoryMovement(){}

    public InventoryMovement(Integer inventoryMovementId){
        this.inventoryMovementId = inventoryMovementId;
    }

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
    public void setInventoryMovementId(Integer inventoryMovementId){
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

    public void setReserveChange(Integer reservedChange) {
        this.reservedChange = reservedChange;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setReason(MovementReason reason) {
        this.reason = reason;
    }
}
