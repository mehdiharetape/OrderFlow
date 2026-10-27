package com.example.order_flow.product.integration;

import com.example.order_flow.dto.product.UpdateProductNamePriceDto;
import com.example.order_flow.entity.Product;
import com.example.order_flow.entity.enums.ProductStatus;
import com.example.order_flow.exceptions.ProductExistsException;
import com.example.order_flow.exceptions.ProductNotFoundException;
import com.example.order_flow.product.unit.UpdateProductNamePriceUnitTest;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.text.html.Option;

import java.awt.font.OpenType;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class UpdateNamePriceProductIntegrationTest {
    @Autowired
    private ProductRepository repository;
    @Autowired
    private ProductService service;

    @Test
    public void updateNamePriceProduct(){
        Integer productId = 1001;
        var updateDto = new UpdateProductNamePriceDto();
        updateDto.setName("Test-Product-Update");
        updateDto.setNewtPrice(59.32);

        Optional<Product> oldProduct = repository.findById(productId);
        service.updateNamePrice(productId, updateDto);
        Optional<Product> newProduct = repository.findById(productId);

        assertNotEquals(oldProduct.get().getName(), newProduct.get().getName());
        assertNotEquals(oldProduct.get().getCurrentPrice(), newProduct.get().getCurrentPrice());
        assertEquals(oldProduct.get().getStatus(), newProduct.get().getStatus());
    }

    @Test
    public void productNotFoundTest(){
        Integer productId = 10000;
        var updateDto = new UpdateProductNamePriceDto();
        updateDto.setName("Test-Price-Changed");
        updateDto.setNewtPrice(50.32);

        assertThrows(ProductNotFoundException.class,
                () -> service.updateNamePrice(productId, updateDto));
    }
}
