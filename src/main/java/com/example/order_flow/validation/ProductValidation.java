package com.example.order_flow.validation;

import com.example.order_flow.dto.product.CreateProductDto;
import com.example.order_flow.dto.product.UpdateProductNamePriceDto;
import com.example.order_flow.dto.product.UpdateProductStatusDto;
import com.example.order_flow.entity.enums.ProductStatus;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;

public class ProductValidation {

    //create validation
    public static void createProductValidation(CreateProductDto dto){
        String name = dto.getName();
        double currentPrice = dto.getCurrentPrice();

        if(name == null || name.isEmpty())
            throw new IllegalOrNullArgumentException();

        if (currentPrice <= 0)
            throw new IllegalOrNullArgumentException();
    }

    //update validation
    public static void updateNamePriceValidation(UpdateProductNamePriceDto dto){
        String name = dto.getName();
        double currentPrice = dto.getNewPrice();

        if(name == null || name.isEmpty())
            throw new IllegalOrNullArgumentException();

        if (currentPrice <= 0)
            throw new IllegalOrNullArgumentException();
    }

    //update status validation
    public static void UpdateStatusValidation(UpdateProductStatusDto statusDto)
    {
        ProductStatus newStatus = statusDto.getStatus();

        if (newStatus == null)
            throw new IllegalOrNullArgumentException();

    }

    public static void productIdValidation(Integer productId){
        if(productId == null || productId < 1000)
            throw new IllegalOrNullArgumentException();
    }
}
