package com.example.order_flow.product.integration;

import com.example.order_flow.dto.product.CreateProductDto;
import com.example.order_flow.entity.Product;
import com.example.order_flow.entity.enums.ProductStatus;
import com.example.order_flow.exceptions.ProductExistsException;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CreateProductIntegrationTest {
    @Autowired
    private ProductRepository repository;
    @Autowired
    private ProductService service;

    @Test
    public void createProductTest(){
        CreateProductDto productDto = new CreateProductDto();
        productDto.setName("Test-Product");
        productDto.setCurrentPrice(13.67);

        Integer resultId = service.createProduct(productDto);

        Optional<Product> product = repository.findById(resultId);

        assertNotNull(resultId);
        assertTrue(product.isPresent());
        assertEquals(resultId, product.get().getProductId());
        assertEquals(ProductStatus.INACTIVE, product.get().getStatus());
    }
}
