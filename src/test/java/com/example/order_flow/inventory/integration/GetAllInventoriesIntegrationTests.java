package com.example.order_flow.inventory.integration;

import com.example.order_flow.dto.inventory.InventoryDetailResponseDto;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GetAllInventoriesIntegrationTests {
    @Autowired
    private InventoryService service;

    @Test
    public void getAllInventoriesHappyFlow(){
        List<InventoryDetailResponseDto> result
                = service.getAllInventories();

        assertNotNull(result);
        assertEquals(2008, result.get(0).getInventoryId());
    }
}
