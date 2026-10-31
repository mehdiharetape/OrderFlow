package com.example.order_flow.movement.integration;

import com.example.order_flow.dto.movement.InventoryMovementResponseDto;
import com.example.order_flow.service.InventoryMovementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GetAllMovementsByOrderIdIntegrationTests {
    @Autowired
    private InventoryMovementService movementService;

    @Test
    public void getAllMovementsByOrderIdHappyFlow(){
        Integer oid = 3023;

        List<InventoryMovementResponseDto> result =
                movementService.getMovementListByOrderId(oid);

        assertEquals(4, result.size());
        assertEquals(oid, result.get(0).getOrderId());
        assertEquals(oid, result.get(1).getOrderId());
        assertEquals(oid, result.get(2).getOrderId());
        assertEquals(oid, result.get(3).getOrderId());
    }
}
