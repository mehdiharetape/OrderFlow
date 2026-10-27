package com.example.order_flow.product.integration;

import com.example.order_flow.dto.product.UpdateProductNamePriceDto;
import com.example.order_flow.exceptions.ProductExistsException;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UpdateRepetitiveNamePriceProductTest {
    @Autowired
    private ProductRepository repository;
    @Autowired
    private ProductService service;

    @Test
    public void notAllowedRepetitiveProductName(){
        Integer productId = 1001;
        var updateDto = new UpdateProductNamePriceDto();
        updateDto.setName("Test-2");
        updateDto.setNewtPrice(10.65);

        assertThrows(ProductExistsException.class,
                () -> service.updateNamePrice(productId, updateDto));
    }
}
