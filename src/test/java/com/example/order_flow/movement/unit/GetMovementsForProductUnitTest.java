package com.example.order_flow.movement.unit;

import com.example.order_flow.dto.movement.InventoryMovementResponseDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.entity.enums.MovementReason;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.MovementNotExistsForProductException;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.service.InventoryMovementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GetMovementsForProductUnitTest {
    @Mock
    private InventoryMovementRepository movementRepository;
    @InjectMocks
    private InventoryMovementService service;

    @Test
    public void getMovementsForProductHappyFlow(){
        Integer pid = 1001;

        var im1 = new InventoryMovement(5001);
        im1.setOrderId(null);
        im1.setProductId(1001);
        im1.setPhysicalChange(0);
        im1.setReserveChange(3);
        im1.setCreatedAt(LocalDateTime.now());
        im1.setReason(MovementReason.RESERVATION);

        var im2 = new InventoryMovement(5002);
        im2.setOrderId(null);
        im2.setProductId(1001);
        im2.setPhysicalChange(3);
        im2.setReserveChange(0);
        im2.setCreatedAt(LocalDateTime.now());
        im2.setReason(MovementReason.INITIAL_STOCK);

        given(movementRepository.findMovementsByProductId(pid))
                .willReturn(List.of(im1, im2));

        List<InventoryMovementResponseDto> result =
                service.getMovementListByProductId(pid);

        assertEquals(2, result.size());
        assertEquals(pid, result.get(0).getProductId());
        assertEquals(pid, result.get(1).getProductId());
    }

    @Test
    public void emptyMovementListTest(){
        Integer pid = 1001;

        given(movementRepository.findMovementsByProductId(pid))
                .willReturn(List.of());

        List<InventoryMovementResponseDto> result =
                service.getMovementListByProductId(pid);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void nullProductIdTest(){
        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.getMovementListByProductId(null));
    }

    @Test
    public void invalidProductIdTest(){
        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.getMovementListByProductId(999));
    }
}
