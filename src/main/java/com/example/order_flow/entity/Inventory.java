package com.example.order_flow.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("inventory")
public class Inventory {
    @Id
    private Integer inventoryId;
    private Integer productId;
    private Integer totalQuantity;
    private Integer reservedQuantity;

    public Inventory(){}

    public Inventory(Integer inventoryId){
        this.inventoryId = inventoryId;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public Integer getProductId() {
        return productId;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    //setters
    public void setInventoryId(Integer inventoryId){
        this.inventoryId = inventoryId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }
}
