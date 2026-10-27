package com.example.order_flow.dto.inventory;

public class InventoryDetailResponseDto {
    private int inventoryId;
    private int productId;
    private int totalQuantity;
    private int reservedQuantity;
    private int available;

    //getters
    public int getInventoryId(){
        return inventoryId;
    }

    public int getProductId(){
        return productId;
    }

    public int getTotalQuantity(){
        return totalQuantity;
    }

    public int getReservedQuantity(){
        return reservedQuantity;
    }

    public int getAvailable(){
        return available;
    }

    //setters
    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setReservedQuantity(int reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public void setAvailable(int available) {
        this.available = available;
    }
}
