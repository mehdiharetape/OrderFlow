package com.example.order_flow.dto.order;

import org.springframework.data.relational.core.sql.In;

public class OrderItemRequestDto {
    private Integer productId;
    private Integer quantity;

    //getters
    public Integer getProductId(){
        return productId;
    }

    public Integer getQuantity(){
        return quantity;
    }

    //setters
    public void setProductId(Integer productId){
        this.productId = productId;
    }

    public void setQuantity(Integer quantity){
        this.quantity = quantity;
    }
}
