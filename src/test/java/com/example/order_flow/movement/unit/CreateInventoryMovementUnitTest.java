package com.example.order_flow.movement.unit;

import com.example.order_flow.dto.movement.CreateMovementDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.entity.enums.MovementReason;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.OrderNotFoundException;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.service.InventoryMovementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateInventoryMovementUnitTest {
    @Mock
    private InventoryMovementRepository movementRepository;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private InventoryMovementService movementService;

    @Test
    public void createInventoryMovementHappyFlow()
    {
        CreateMovementDto dto = new CreateMovementDto();
        dto.setOrderId(3001);
        dto.setProductId(1001);
        dto.setPhysicalChange(0);
        dto.setReservedChange(2);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setReason(MovementReason.RESERVATION);

        given(orderRepository.existsById(3001)).willReturn(true);
        when(movementRepository.save(any(InventoryMovement.class)))
                .thenAnswer(invocation -> {
                    InventoryMovement save = invocation.getArgument(0);
                    save.setInventoryMovementId(5001);
                    return save;
                });

        Integer id = movementService.createInventoryMovement(dto);

        assertNotNull(id);
        assertEquals(5001, id);

        verify(movementRepository).save(any(InventoryMovement.class));
        verify(orderRepository).existsById(3001);
    }

    //create initial inventory without order
    @Test
    public void createInitialInventoryWithoutOrder(){
        CreateMovementDto dto = new CreateMovementDto();
        dto.setOrderId(null);
        dto.setProductId(1001);
        dto.setPhysicalChange(2);
        dto.setReservedChange(0);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setReason(MovementReason.INITIAL_STOCK);

        when(movementRepository.save(any(InventoryMovement.class)))
                .thenAnswer(invocation -> {
                    InventoryMovement save = invocation.getArgument(0);
                    save.setInventoryMovementId(5001);
                    return save;
                });

        Integer id = movementService.createInventoryMovement(dto);

        assertNotNull(id);
        assertEquals(5001, id);

        verify(movementRepository).save(any(InventoryMovement.class));
        verify(orderRepository, never()).existsById(3500);
    }

    //create movement for change quantity without order
    @Test
    public void changeInventoryQuantityWithoutOrder(){
        CreateMovementDto dto = new CreateMovementDto();
        dto.setOrderId(null);
        dto.setProductId(1001);
        dto.setPhysicalChange(2);
        dto.setReservedChange(0);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setReason(MovementReason.CHANGE_TOTAL_QUANTITY);

        when(movementRepository.save(any(InventoryMovement.class)))
                .thenAnswer(invocation -> {
                    InventoryMovement save = invocation.getArgument(0);
                    save.setInventoryMovementId(5001);
                    return save;
                });

        Integer id = movementService.createInventoryMovement(dto);

        assertNotNull(id);
        assertEquals(5001, id);

        verify(movementRepository).save(any(InventoryMovement.class));
        verify(orderRepository, never()).existsById(3500);
    }

    //null order id for not initial stock or change quantity reason
    @Test
    public void createInventoryMovementWithoutOrderAnotherReason(){
        CreateMovementDto dto = new CreateMovementDto();
        dto.setOrderId(null);
        dto.setProductId(1001);
        dto.setPhysicalChange(0);
        dto.setReservedChange(2);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setReason(MovementReason.RESERVATION);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> movementService.createInventoryMovement(dto));

        verify(movementRepository, never()).save(any(InventoryMovement.class));
    }

    //null reason
    @Test
    public void nullMovementReasonTest(){
        CreateMovementDto dto = new CreateMovementDto();
        dto.setOrderId(3001);
        dto.setProductId(1001);
        dto.setPhysicalChange(0);
        dto.setReservedChange(2);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setReason(null);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> movementService.createInventoryMovement(dto));

        verify(movementRepository, never()).save(any(InventoryMovement.class));
    }
    //null created at
    @Test
    public void nullCreatedAtTest(){
        CreateMovementDto dto = new CreateMovementDto();
        dto.setOrderId(3001);
        dto.setProductId(1001);
        dto.setPhysicalChange(0);
        dto.setReservedChange(2);
        dto.setCreatedAt(null);
        dto.setReason(MovementReason.RESERVATION);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> movementService.createInventoryMovement(dto));

        verify(movementRepository, never()).save(any(InventoryMovement.class));
    }

    //null or invalid product id
    @Test
    public void nullProductIdTest(){
        CreateMovementDto dto = new CreateMovementDto();
        dto.setOrderId(3001);
        dto.setProductId(null);
        dto.setPhysicalChange(0);
        dto.setReservedChange(2);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setReason(MovementReason.RESERVATION);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> movementService.createInventoryMovement(dto));

        verify(movementRepository, never()).save(any(InventoryMovement.class));
    }

    @Test
    public void invalidProductIdTest(){
        CreateMovementDto dto = new CreateMovementDto();
        dto.setOrderId(3001);
        dto.setProductId(999);
        dto.setPhysicalChange(0);
        dto.setReservedChange(2);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setReason(MovementReason.RESERVATION);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> movementService.createInventoryMovement(dto));

        verify(movementRepository, never()).save(any(InventoryMovement.class));
    }

    //order not found
    @Test
    public void productNotFoundTest(){
        CreateMovementDto dto = new CreateMovementDto();
        dto.setOrderId(3500);
        dto.setProductId(1001);
        dto.setPhysicalChange(0);
        dto.setReservedChange(2);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setReason(MovementReason.RESERVATION);

        given(orderRepository.existsById(3500)).willReturn(false);

        assertThrows(OrderNotFoundException.class,
                () -> movementService.createInventoryMovement(dto));

        verify(movementRepository, never()).save(any(InventoryMovement.class));
        verify(orderRepository).existsById(3500);
    }
}
