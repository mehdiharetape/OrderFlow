package com.example.order_flow.movement.integration;

import com.example.order_flow.dto.movement.CreateMovementDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.entity.enums.MovementReason;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.OrderNotFoundException;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.service.InventoryMovementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CreateInventoryMovementIntegrationTest {
    @Autowired
    private InventoryMovementRepository movementRepository;
    @Autowired
    private InventoryMovementService movementService;

    //@Test
    public void createInventoryMovementHappyFlow(){
        CreateMovementDto dto = new CreateMovementDto();
        dto.setOrderId(3018);
        dto.setProductId(1001);
        dto.setPhysicalChange(0);
        dto.setReservedChange(2);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setReason(MovementReason.RESERVATION);

        Integer mid = movementService.createInventoryMovement(dto);
        Optional<InventoryMovement> found = movementRepository.findById(mid);

        assertNotNull(mid);
        assertEquals(mid, found.get().getInventoryMovementId());
        assertEquals(MovementReason.RESERVATION ,found.get().getReason());
        assertEquals(0, found.get().getPhysicalChange());
        assertEquals(2, found.get().getReservedChange());
        assertEquals(3018, found.get().getOrderId());
    }

    //initial stock
    //@Test
    public void createInventoryMovementInitialStock(){
        CreateMovementDto dto = new CreateMovementDto();
        dto.setOrderId(null);
        dto.setProductId(1001);
        dto.setPhysicalChange(2);
        dto.setReservedChange(0);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setReason(MovementReason.INITIAL_STOCK);

        Integer mid = movementService.createInventoryMovement(dto);
        Optional<InventoryMovement> found = movementRepository.findById(mid);

        assertNotNull(mid);
        assertEquals(mid, found.get().getInventoryMovementId());
        assertEquals(MovementReason.INITIAL_STOCK ,found.get().getReason());
        assertEquals(2, found.get().getPhysicalChange());
        assertEquals(0, found.get().getReservedChange());
        assertNull(found.get().getOrderId());
    }

    //fail null order id
    //@Test
    public void failCreateInventoryMovementNullOrder(){
        CreateMovementDto dto = new CreateMovementDto();
        dto.setOrderId(null);
        dto.setProductId(1001);
        dto.setPhysicalChange(2);
        dto.setReservedChange(0);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setReason(MovementReason.RESERVATION);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> movementService.createInventoryMovement(dto));
    }

    //order not found
    //@Test
    public void orderNotFoundTest(){
        CreateMovementDto dto = new CreateMovementDto();
        dto.setOrderId(3500);
        dto.setProductId(1001);
        dto.setPhysicalChange(2);
        dto.setReservedChange(0);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setReason(MovementReason.RESERVATION);

        assertThrows(OrderNotFoundException.class,
                () -> movementService.createInventoryMovement(dto));
    }
}
