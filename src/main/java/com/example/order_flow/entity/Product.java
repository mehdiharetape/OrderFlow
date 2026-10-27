package com.example.order_flow.entity;

import com.example.order_flow.entity.enums.ProductStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.sql.In;

import java.math.BigDecimal;

@Table("product")
public class Product {
    @Id
    private Integer productId;
    private String name;
    private ProductStatus status;
    private BigDecimal currentPrice;

    public Product() {}

    public Product(Integer productId){
        this.productId = productId;
    }

    //getters
    public Integer getProductId(){
        return productId;
    }

    public String getName(){
        return name;
    }

    public ProductStatus getStatus(){
        return status;
    }

    public BigDecimal getCurrentPrice(){
        return currentPrice;
    }

    //setters
    public void setName(String name){
        this.name = name;
    }

    public void setStatus(ProductStatus status){
        this.status = status;
    }

    public void setCurrentPrice(BigDecimal currentPrice){
        this.currentPrice = currentPrice;
    }
}
