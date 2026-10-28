package com.example.order_flow.inventory.integration;

import com.example.order_flow.dto.inventory.InventoryDetailResponseDto;
import com.example.order_flow.exceptions.InventoryNotFoundException;
import com.example.order_flow.exceptions.InventoryNotFoundForProductException;
import com.example.order_flow.exceptions.ProductNotFoundException;
import com.example.order_flow.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GetInventoryByProductIdIntegrationTest {
    @Autowired
    private InventoryService service;

    @Test
    public void getInventoryByProductIdHappyFlow(){
        Integer pid = 1002;
        InventoryDetailResponseDto result =
                service.getInventoryByProductId(pid);

        assertEquals(pid, result.getProductId());
        assertEquals(2008, result.getInventoryId());
    }

    @Test //product not found
    public void getInventoryProductNotFoundTest(){
        Integer pid = 11000; //valid but not found

        assertThrows(ProductNotFoundException.class,
                () -> service.getInventoryByProductId(pid));
    }

    @Test //product id is valid but inventory not found for this product id
    public void inventoryNotFoundTest(){
        Integer pid = 1005;
        assertThrows(InventoryNotFoundForProductException.class,
                () -> service.getInventoryByProductId(pid));
    }
}
