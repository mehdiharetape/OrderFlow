package com.example.order_flow.product.integration;

import com.example.order_flow.dto.product.ProductDetailResponseDto;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class GetEmptyProductListIntegrationTest {
    @Autowired
    private ProductRepository repository;
    @Autowired
    private ProductService service;

    @Test
    public void getEmptyProductListTest(){
        List<ProductDetailResponseDto> products =
                service.getAllProducts();

        assertNotNull(products);
        assertTrue(products.isEmpty());
    }
}
