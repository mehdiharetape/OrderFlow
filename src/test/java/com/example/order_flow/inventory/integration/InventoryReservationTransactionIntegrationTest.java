package com.example.order_flow.inventory.integration;

import com.example.order_flow.dto.inventory.ReservationChangeDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.repository.InventoryMovementRepository;
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
    private InventoryMovementRepository movementRepository;
    @Autowired
    private InventoryService service;

    @Test
    public void movementShouldRollbackWhenExceptionThrows(){
        doThrow(new RuntimeException("Simulated Exception"))
                .when(movementRepository).save(any(InventoryMovement.class));

        Integer pi = 1001;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setReserveAmount(2);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.reservation(pi, dto));

        assertEquals("Simulated Exception", exception.getMessage());

        verify(movementRepository).save(any(InventoryMovement.class));
    }
}
