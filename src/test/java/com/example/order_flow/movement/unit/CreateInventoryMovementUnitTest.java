package com.example.order_flow.movement.unit;

import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.service.InventoryMovementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateInventoryMovementUnitTest {
    @Mock
    private InventoryMovementRepository movementRepository;
    @InjectMocks
    private InventoryMovementService movementService;

//    @Test
//    public void createInventoryMovementHappyFlow(){
//        Integer pid = 1001;
//        Integer q = 5;
//
//        when(movementRepository.save(any(InventoryMovement.class)))
//                .thenAnswer(invocation -> {
//                    InventoryMovement save = invocation.getArgument(0);
//                    save.setInventoryMovementId(5001);
//                    return save;
//                });
//
//        Integer id = movementService.createInventoryMovement(pid, q);
//
//        assertNotNull(id);
//        assertEquals(5001, id);
//
//        verify(movementRepository).save(any(InventoryMovement.class));
//    }
//
//    //zero total quantity
//    @Test
//    public void zeroTotalQuantityTest(){
//        Integer pid = 1001;
//        Integer q = 0;
//
//        Integer id = movementService.createInventoryMovement(pid, q);
//
//        assertNull(id);
//
//        verify(movementRepository, never()).save(any(InventoryMovement.class));
//    }
//
//    //negative total quantity
//    @Test
//    public void negativeTotalQuantityTest(){
//        Integer pid = 1001;
//        Integer q = -1;
//
//        Integer id = movementService.createInventoryMovement(pid, q);
//
//        assertNull(id);
//
//        verify(movementRepository, never()).save(any(InventoryMovement.class));
//    }
//
//    //null or invalid product id
//    @Test
//    public void nullProductIdTest(){
//        Integer pid = null;
//        Integer q = 5;
//
//        assertThrows(IllegalOrNullArgumentException.class,
//                () -> movementService.createInventoryMovement(pid, q));
//
//        verify(movementRepository, never()).save(any(InventoryMovement.class));
//    }
//
//    @Test
//    public void invalidProductIdTest(){
//        Integer pid = 999;
//        Integer q = 5;
//
//        assertThrows(IllegalOrNullArgumentException.class,
//                () -> movementService.createInventoryMovement(pid, q));
//
//        verify(movementRepository, never()).save(any(InventoryMovement.class));
//    }
}
