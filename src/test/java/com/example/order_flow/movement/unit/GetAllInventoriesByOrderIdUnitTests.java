package com.example.order_flow.movement.unit;

import com.example.order_flow.dto.movement.InventoryMovementResponseDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.entity.enums.MovementReason;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.service.InventoryMovementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GetAllInventoriesByOrderIdUnitTests {
    @Mock
    private InventoryMovementRepository movementRepository;
    @InjectMocks
    private InventoryMovementService movementService;

    @Test
    public void getAllMovementByOrderIdHappyFlow(){
        Integer oid = 3001;

        var im1 = new InventoryMovement(5001);
        im1.setOrderId(oid);
        im1.setProductId(1001);
        im1.setPhysicalChange(0);
        im1.setReserveChange(3);
        im1.setCreatedAt(LocalDateTime.now());
        im1.setReason(MovementReason.RESERVATION);

        var im2 = new InventoryMovement(5002);
        im2.setOrderId(oid);
        im2.setProductId(1002);
        im2.setPhysicalChange(3);
        im2.setReserveChange(0);
        im2.setCreatedAt(LocalDateTime.now());
        im2.setReason(MovementReason.RESERVATION);

        given(movementRepository.findMovementsByOrderId(oid))
                .willReturn(List.of(im1, im2));

        List<InventoryMovementResponseDto> result =
                movementService.getMovementListByOrderId(oid);

        assertEquals(2, result.size());
        assertEquals(oid, result.get(0).getOrderId());
        assertEquals(oid, result.get(1).getOrderId());
    }

    //empty movement list
    @Test
    public void emptyMovementListByOrderIdHappyFlow(){
        Integer oid = 3001;

        given(movementRepository.findMovementsByOrderId(oid))
                .willReturn(List.of());

        List<InventoryMovementResponseDto> result =
                movementService.getMovementListByOrderId(oid);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //null or invalid order id
    @Test
    public void nullProductIdTest(){
        assertThrows(IllegalOrNullArgumentException.class,
                () -> movementService.getMovementListByOrderId(null));
    }

    @Test
    public void invalidProductIdTest(){
        assertThrows(IllegalOrNullArgumentException.class,
                () -> movementService.getMovementListByOrderId(2999));
    }
}
