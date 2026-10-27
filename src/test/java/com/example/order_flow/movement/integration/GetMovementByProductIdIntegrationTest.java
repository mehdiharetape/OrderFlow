package com.example.order_flow.movement.integration;

import com.example.order_flow.dto.movement.InventoryMovementResponseDto;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.service.InventoryMovementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GetMovementByProductIdIntegrationTest {
    @Autowired
    private InventoryMovementService service;
    @Autowired
    private InventoryMovementRepository repository;

    @Test
    public void getMovementsByProductIdTest(){
        List<InventoryMovementResponseDto> result =
                service.getMovementListByProductId(1001);

        assertNotNull(result);
        assertEquals(1001, result.get(0).getProductId());
    }

    @Test
    public void getEmptyListMovementsForNotFoundProductIdTest(){
        List<InventoryMovementResponseDto> result =
                service.getMovementListByProductId(1100);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
