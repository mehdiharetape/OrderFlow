package com.example.order_flow.dto.product;

public class UpdateProductNamePriceDto {
    private String name;
    private double newPrice;

    public String getName() {
        return name;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNewtPrice(double newPrice) {
        this.newPrice = newPrice;
    }
}
