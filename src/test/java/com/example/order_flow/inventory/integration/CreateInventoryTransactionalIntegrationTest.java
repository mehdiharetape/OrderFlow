package com.example.order_flow.inventory.integration;

import com.example.order_flow.dto.inventory.CreateInventoryDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.repository.ProductRepository;
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
    @Autowired
    private InventoryRepository inventoryRepository;
    @MockitoSpyBean
    private InventoryMovementRepository movementRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryService service;

    @Test
    public void createInventoryShouldRollbackWhenMovementSaveFails(){
        assertFalse(inventoryRepository.existsByProductId(1002));
        doThrow(new RuntimeException("Simulated movement failure"))
                .when(movementRepository).save(any(InventoryMovement.class));


        CreateInventoryDto dto = new CreateInventoryDto();
        dto.setProductId(1002);
        dto.setTotalQuantity(15);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.createInventory(dto));

        assertEquals("Simulated movement failure",
                exception.getMessage());

        verify(movementRepository)
                .save(any(InventoryMovement.class));

        assertFalse(inventoryRepository.existsByProductId(1002));
    }
}
