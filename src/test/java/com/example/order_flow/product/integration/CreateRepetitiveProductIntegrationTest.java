package com.example.order_flow.product.integration;

import com.example.order_flow.dto.product.CreateProductDto;
import com.example.order_flow.exceptions.ProductExistsException;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class CreateRepetitiveProductIntegrationTest {
    @Autowired
    private ProductRepository repository;
    @Autowired
    private ProductService service;

    @Test
    public void createRepetitiveProductTest(){
        //it's a repetitive product
        CreateProductDto productDto = new CreateProductDto();
        productDto.setName("Test-Product");
        productDto.setCurrentPrice(13.67);

        assertThrows(ProductExistsException.class,
                ()->service.createProduct(productDto));
    }
}
