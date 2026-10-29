package com.example.order_flow.inventory.integration;

import com.example.order_flow.dto.inventory.ReservationChangeDto;
import com.example.order_flow.dto.movement.CreateMovementDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.service.InventoryMovementService;
import com.example.order_flow.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class InventoryReservationTransactionIntegrationTest {
    @MockitoSpyBean
    private InventoryMovementService movementService;
    @Autowired
    private InventoryService service;
    @Autowired
    private InventoryMovementRepository movementRepository;


    @Test
    public void movementShouldRollbackWhenExceptionThrows(){
        doThrow(new RuntimeException("Simulated Exception"))
                .when(movementService).createInventoryMovement(
                        any(CreateMovementDto.class));

        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setOrderId(3001);
        dto.setReserveAmount(2);

        assertThrows(RuntimeException.class,
                () -> service.reservation(1004, dto));


        assertEquals(0, movementRepository.countByProductId(1004));
    }
}
