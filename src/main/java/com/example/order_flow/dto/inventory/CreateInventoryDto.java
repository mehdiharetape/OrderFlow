package com.example.order_flow.dto.inventory;

public class CreateInventoryDto {
    private int productId;
    private int totalQuantity;

    public int getTotalQuantity(){
        return totalQuantity;
    }

    public int getProductId(){
        return productId;
    }

    public void setProductId(int productId){
        this.productId = productId;
    }

    public void setTotalQuantity(int totalQuantity){
        this.totalQuantity = totalQuantity;
    }
}
