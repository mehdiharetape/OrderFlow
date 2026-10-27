package com.example.order_flow.inventory.integration;

import com.example.order_flow.dto.inventory.ReservationChangeDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.entity.enums.MovementReason;
import com.example.order_flow.exceptions.InvalidReservationAmountException;
import com.example.order_flow.exceptions.InventoryNotFoundForProductException;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.service.InventoryService;
import com.example.order_flow.utils.InventoryResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CancelReservationIntegrationTests {
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private InventoryMovementRepository movementRepository;
    @Autowired
    private InventoryService service;

    //@Test
    public void cancelReservationInventoryHappyFlow(){
        Integer pid = 1001;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setReserveAmount(4);

        InventoryResult result = service.cancelReservation(pid,dto);
        Optional<InventoryMovement> movement = movementRepository.findById(result.inventoryMovement());

        assertNotNull(result);
        assertEquals(2009, result.inventory());
        assertEquals(result.inventoryMovement(),
                movement.get().getInventoryMovementId());
        assertEquals(MovementReason.CANCEL_RESERVATION, movement.get().getReason());
    }

    @Test
    public void invalidReservationCancelAmount(){
        Integer pid = 1001;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setReserveAmount(6);

        assertThrows(InvalidReservationAmountException.class,
                () -> service.cancelReservation(pid, dto));
    }

    @Test
    public void inventoryNotFoundForProduct(){
        Integer pid = 120000;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setReserveAmount(6);

        assertThrows(InventoryNotFoundForProductException.class,
                () -> service.cancelReservation(pid, dto));
    }
}
