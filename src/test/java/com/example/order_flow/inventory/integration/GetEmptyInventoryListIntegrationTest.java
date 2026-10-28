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
public class GetEmptyInventoryListIntegrationTest {
    @Autowired
    private InventoryService service;

    @Test
    public void emptyInventoryListTest(){
        List<InventoryDetailResponseDto> result
                = service.getAllInventories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
