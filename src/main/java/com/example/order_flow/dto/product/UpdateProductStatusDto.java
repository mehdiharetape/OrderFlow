package com.example.order_flow.dto.product;

import com.example.order_flow.entity.enums.ProductStatus;

public class UpdateProductStatusDto {
    private ProductStatus status;

    public ProductStatus getStatus(){
        return status;
    }

    public void setStatus(ProductStatus status){
        this.status = status;
    }
}
