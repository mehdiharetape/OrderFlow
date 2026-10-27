package com.example.order_flow.mapper;

import com.example.order_flow.dto.product.CreateProductDto;
import com.example.order_flow.dto.product.ProductDetailResponseDto;
import com.example.order_flow.entity.Product;
import com.example.order_flow.entity.enums.ProductStatus;

import java.math.BigDecimal;

public class ProductMapper {
    //CreateProductDto to Product
    public static Product createDtoToProduct(CreateProductDto dto){
        Product product = new Product();

        product.setName(dto.getName());
        product.setStatus(ProductStatus.INACTIVE);
        product.setCurrentPrice(BigDecimal.valueOf(dto.getCurrentPrice()));

        return product;
    }

    //Product To ProductDetailResponseDto
    public static ProductDetailResponseDto productToDetailDto(Product product){
        ProductDetailResponseDto dto = new ProductDetailResponseDto();

        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setStatus(product.getStatus().toString());
        dto.setCurrentPrice(product.getCurrentPrice());

        return dto;
    }
}
