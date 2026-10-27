package com.example.order_flow.product.integration;

import com.example.order_flow.dto.product.UpdateProductStatusDto;
import com.example.order_flow.entity.Product;
import com.example.order_flow.entity.enums.ProductStatus;
import com.example.order_flow.exceptions.ProductNotFoundException;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UpdateProductStatusIntegrationTest {
    @Autowired
    private ProductRepository repository;
    @Autowired
    private ProductService service;

    @Test
    public void updateActiveToInactiveStatusTest(){
        Integer id = 1001;
        var dto = new UpdateProductStatusDto();
        dto.setStatus(ProductStatus.ACTIVE);

        Optional<Product> oldProduct = repository.findById(id);
        service.updateProductStatus(id, dto);
        Optional<Product> newProduct = repository.findById(id);

        assertEquals(oldProduct.get().getName(), newProduct.get().getName());
        assertEquals(oldProduct.get().getCurrentPrice(), newProduct.get().getCurrentPrice());
        assertNotEquals(oldProduct.get().getStatus(), newProduct.get().getStatus());
    }

    @Test
    public void updateInactiveToActiveStatusTest(){
        Integer id = 1002;
        var dto = new UpdateProductStatusDto();
        dto.setStatus(ProductStatus.INACTIVE);

        Optional<Product> oldProduct = repository.findById(id);
        service.updateProductStatus(id, dto);
        Optional<Product> newProduct = repository.findById(id);

        assertEquals(oldProduct.get().getName(), newProduct.get().getName());
        assertEquals(oldProduct.get().getCurrentPrice(), newProduct.get().getCurrentPrice());
        assertNotEquals(oldProduct.get().getStatus(), newProduct.get().getStatus());
    }

    @Test
    public void productNotFoundTest(){
        Integer id = 100000;
        var dto = new UpdateProductStatusDto();
        dto.setStatus(ProductStatus.INACTIVE);

        assertThrows(ProductNotFoundException.class,
                () -> service.updateProductStatus(id, dto));
    }
}
