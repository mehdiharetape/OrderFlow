package com.example.order_flow.inventory.integration;

import com.example.order_flow.dto.inventory.ChangeTotalQuantityDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ChangeQuantityTransactionIntegrationTest {
    @Autowired
    private InventoryService service;
    @MockitoSpyBean
    private InventoryMovementRepository movementRepository;
    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    public void changeTotalQuantityShouldRollbackWhenMovementSaveFails(){
        doThrow(new RuntimeException("Simulated Exception")).
                when(movementRepository).save(any(InventoryMovement.class));

        ChangeTotalQuantityDto dto = new ChangeTotalQuantityDto();
        dto.setAmount(-5);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.changeTotalQuantity(2009, dto));

        assertEquals("Simulated Exception", exception.getMessage());

        verify(movementRepository).save(any(InventoryMovement.class));
    }
}
