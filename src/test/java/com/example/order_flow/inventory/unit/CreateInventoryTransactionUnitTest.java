package com.example.order_flow.inventory.unit;

import com.example.order_flow.dto.inventory.CreateInventoryDto;
import com.example.order_flow.dto.movement.CreateMovementDto;
import com.example.order_flow.entity.Inventory;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.InventoryMovementService;
import com.example.order_flow.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CreateInventoryTransactionUnitTest {
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryMovementService movementService;
    @Mock
    private ProductRepository productRepository;

    private InventoryService service;
    private final Clock clock = Clock.fixed(
            Instant.parse("2026-11-01T10:00:00Z"),
            ZoneOffset.UTC
    );

    @BeforeEach
    public void setUp(){
        service = new InventoryService(inventoryRepository,
                productRepository, movementService, clock);
    }

    @Test
    public void transactionRollBackCreateInventory(){
        CreateInventoryDto dto = new CreateInventoryDto();
        dto.setProductId(1001);
        dto.setTotalQuantity(10);

        when(productRepository.existsById(1001)).thenReturn(true);
        when(inventoryRepository.existsByProductId(1001)).thenReturn(false);

        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> {
                    Inventory save = invocation.getArgument(0);
                    save.setInventoryId(2000);
                    return save;
                });


        when(movementService.createInventoryMovement(any(CreateMovementDto.class)))
                .thenThrow(new RuntimeException());


        assertThrows(RuntimeException.class,
                () -> service.createInventory(dto));

        verify(productRepository).existsById(1001);
        verify(inventoryRepository).save(any(Inventory.class));
    }
}
