package com.example.order_flow.dto.product;

import java.math.BigDecimal;

public class ProductDetailResponseDto {
    private Integer productId;
    private String name;
    private String status;
    private BigDecimal currentPrice;

    //getters
    public Integer getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    //setters

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
}
