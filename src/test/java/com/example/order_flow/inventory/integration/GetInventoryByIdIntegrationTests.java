package com.example.order_flow.inventory.integration;

import com.example.order_flow.dto.inventory.InventoryDetailResponseDto;
import com.example.order_flow.exceptions.InventoryNotFoundException;
import com.example.order_flow.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GetInventoryByIdIntegrationTests {
    @Autowired
    private InventoryService service;

    @Test
    public void getInventoryByIdHappyFlow(){
        Integer id = 2008;

        InventoryDetailResponseDto result =
                service.getInventoryById(id);

        assertEquals(id, result.getInventoryId());
        assertEquals(6, result.getAvailable());
    }


    @Test
    public void inventoryNotFoundTest(){
        Integer id = 200000;
        assertThrows(InventoryNotFoundException.class,
                () -> service.getInventoryById(id));
    }
}
