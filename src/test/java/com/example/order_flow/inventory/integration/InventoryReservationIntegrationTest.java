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
public class InventoryReservationIntegrationTest {
    @Autowired
    private InventoryMovementRepository movementRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private InventoryService service;

    //@Test
    public void reservationInventoryHappyFlow(){
        Integer pi = 1001;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setReserveAmount(2);

        InventoryResult result = service.reservation(pi, dto);
        Optional<InventoryMovement> movement = movementRepository.findById(result.inventoryMovement());

        assertNotNull(result);
        assertEquals(2009, result.inventory());
        assertEquals(result.inventoryMovement(), movement.get().getInventoryMovementId());
        assertEquals(MovementReason.RESERVATION, movement.get().getReason());
    }

    @Test
    public void invalidReservationAmountTest(){
        Integer pi = 1002;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setReserveAmount(6);

        assertThrows(InvalidReservationAmountException.class,
                () -> service.reservation(pi, dto));
    }

    @Test
    public void inventoryNotFoundForProductTest(){
        Integer pi = 12222; //valid but not found in inventory
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setReserveAmount(3);

        assertThrows(InventoryNotFoundForProductException.class,
                () -> service.reservation(pi, dto));
    }
}
