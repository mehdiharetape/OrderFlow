package com.example.order_flow.product.integration;

import com.example.order_flow.dto.product.ProductDetailResponseDto;
import com.example.order_flow.entity.enums.ProductStatus;
import com.example.order_flow.exceptions.ProductNotFoundException;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GetProductByIdIntegrationTest {
    @Autowired
    private ProductRepository repository;
    @Autowired
    private ProductService service;

    @Test
    public void getProductByIdTest(){
        Integer productId = 1001;

        ProductDetailResponseDto result
                = service.getProductById(productId);

        assertEquals(productId, result.getProductId());
        assertEquals("Test-Product", result.getName());
        assertEquals(ProductStatus.INACTIVE.toString(), result.getStatus());
    }

    @Test
    public void getProductNotFoundTest(){
        Integer productId = 100000; //this id is valid but not exits in database

        assertThrows(ProductNotFoundException.class,
                () -> service.getProductById(productId));
    }
}
