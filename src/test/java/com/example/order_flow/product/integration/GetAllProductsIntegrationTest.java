package com.example.order_flow.product.integration;

import com.example.order_flow.dto.product.ProductDetailResponseDto;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GetAllProductsIntegrationTest {
    @Autowired
    private ProductRepository repository;
    @Autowired
    private ProductService service;


    @Test
    public void getAllProducts(){
        List<ProductDetailResponseDto> products =
                service.getAllProducts();

        assertNotNull(products);
        assertEquals(1001, products.get(0).getProductId());
    }
}
