package com.example.order_flow.inventory.integration;

import com.example.order_flow.dto.inventory.ChangeTotalQuantityDto;
import com.example.order_flow.dto.inventory.CreateInventoryDto;
import com.example.order_flow.dto.movement.CreateMovementDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.InventoryMovementService;
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
public class CreateInventoryTransactionalIntegrationTest {
    @MockitoSpyBean
    private InventoryMovementService movementService;
    @Autowired
    private InventoryService service;
    @Autowired
    private InventoryMovementRepository movementRepository;

    @Test
    public void createInventoryShouldRollbackWhenMovementSaveFails(){
        doThrow(new RuntimeException("Simulated Exception"))
                .when(movementService).createInventoryMovement(
                        any(CreateMovementDto.class));

        CreateInventoryDto dto = new CreateInventoryDto();
        dto.setTotalQuantity(6);
        dto.setProductId(1005);

        assertThrows(RuntimeException.class,
                () -> service.createInventory(dto));


        assertEquals(0, movementRepository.countByProductId(1005));
    }
}
