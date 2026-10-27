package com.example.order_flow.movement.integration;

import com.example.order_flow.dto.movement.InventoryMovementResponseDto;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.service.InventoryMovementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class GetAllInventoryMovementIntegrationTest {
    @Autowired
    private InventoryMovementService service;
    @Autowired
    private InventoryMovementRepository repository;

    @Test
    public void getAllMovementsTest(){
        List<InventoryMovementResponseDto> result =
                service.getAllMovements();

        assertNotNull(result);
        assertEquals(5003, result.get(0).getInventoryMovementId());
    }
}
