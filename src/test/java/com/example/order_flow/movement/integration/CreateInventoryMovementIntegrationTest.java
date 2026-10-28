package com.example.order_flow.movement.integration;

import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.service.InventoryMovementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CreateInventoryMovementIntegrationTest {
    @Autowired
    private InventoryMovementRepository movementRepository;
    @Autowired
    private InventoryMovementService movementService;

//    @Test
//    public void createInventoryMovementHappyFlow(){
//        Integer pid = 1001;
//        Integer q = 5;
//
//        Integer mid = movementService.createInventoryMovement(pid, q);
//        Optional<InventoryMovement> found = movementRepository.findById(mid);
//
//        assertNotNull(mid);
//        assertEquals(mid, found.get().getInventoryMovementId());
//        assertEquals();
//    }
}
