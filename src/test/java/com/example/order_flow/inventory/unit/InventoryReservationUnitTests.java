package com.example.order_flow.inventory.unit;

import com.example.order_flow.dto.inventory.ReservationChangeDto;
import com.example.order_flow.dto.movement.CreateMovementDto;
import com.example.order_flow.entity.Inventory;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.InvalidReservationAmountException;
import com.example.order_flow.exceptions.InventoryNotFoundForProductException;
import com.example.order_flow.exceptions.OrderNotFoundException;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.service.InventoryMovementService;
import com.example.order_flow.service.InventoryService;
import com.example.order_flow.utils.InventoryResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryReservationUnitTests {
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryMovementService movementService;
    @InjectMocks
    private InventoryService service;

    @Test
    public void reservationChangeHappyFlow(){
        Integer pid = 1001;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setOrderId(3001);
        dto.setReserveAmount(3);

        Inventory inventory = new Inventory(2008);
        inventory.setProductId(pid);
        inventory.setTotalQuantity(10);
        inventory.setReservedQuantity(3);

        given(inventoryRepository.findByProductId(pid)).
                willReturn(Optional.of(inventory));

        when(movementService.createInventoryMovement(any(CreateMovementDto.class)))
                .thenReturn(5001);

        //act
        InventoryResult result = service.reservation(pid, dto);

        assertNotNull(result);
        assertEquals(6, inventory.getReservedQuantity() + 3);
        assertTrue(inventory.getReservedQuantity() + 3 <= 7);
        assertEquals(2008, result.inventory());
        assertEquals(5001, result.inventoryMovement());

        verify(inventoryRepository).findByProductId(pid);
        verify(inventoryRepository).
                changeReservedQuantity(anyInt(), anyInt());
    }

    //zero or negative reservation amount
    @Test
    public void zeroReservationAmountTest(){
        Integer pid = 1001;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setOrderId(3001);
        dto.setReserveAmount(0);

        Inventory inventory = new Inventory(2008);
        inventory.setProductId(pid);
        inventory.setTotalQuantity(10);
        inventory.setReservedQuantity(3);

        given(inventoryRepository.findByProductId(pid)).
                willReturn(Optional.of(inventory));

        //act
        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.reservation(pid, dto));

        verify(inventoryRepository).findByProductId(pid);
        verify(inventoryRepository, never()).
                changeReservedQuantity(anyInt(), anyInt());
    }

    @Test
    public void negativeReservationAmountTest(){
        Integer pid = 1001;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setOrderId(3001);
        dto.setReserveAmount(-1);

        Inventory inventory = new Inventory(2008);
        inventory.setProductId(pid);
        inventory.setTotalQuantity(10);
        inventory.setReservedQuantity(3);

        given(inventoryRepository.findByProductId(pid)).
                willReturn(Optional.of(inventory));

        //act
        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.reservation(pid, dto));

        verify(inventoryRepository).findByProductId(pid);
        verify(inventoryRepository, never()).
                changeReservedQuantity(anyInt(), anyInt());
    }
    //invalid reservation amount
    @Test
    public void invalidReservationAmountTest(){
        Integer pid = 1001;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setOrderId(3001);
        dto.setReserveAmount(9);

        Inventory inventory = new Inventory(2008);
        inventory.setProductId(pid);
        inventory.setTotalQuantity(10);
        inventory.setReservedQuantity(3);

        given(inventoryRepository.findByProductId(pid)).
                willReturn(Optional.of(inventory));

        //act
        assertThrows(InvalidReservationAmountException.class,
                () -> service.reservation(pid, dto));

        verify(inventoryRepository).findByProductId(pid);
        verify(inventoryRepository, never()).
                changeReservedQuantity(anyInt(), anyInt());
    }

    //null product id
    @Test
    public void nullProductIdTest(){
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setReserveAmount(3);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.reservation(null, dto));
    }

    //invalid product id
    @Test
    public void invalidProductIdTest(){
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setReserveAmount(3);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.reservation(999, dto));
    }

    //not found inventory for this product in inventory table
    @Test
    public void notFoundInventoryForProductTest(){
        Integer pid = 16000;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setOrderId(3001);
        dto.setReserveAmount(3);

        given(inventoryRepository.findByProductId(pid))
                .willReturn(Optional.empty());

        assertThrows(InventoryNotFoundForProductException.class,
                () -> service.reservation(pid, dto));

        verify(inventoryRepository).findByProductId(pid);
        verify(inventoryRepository, never()).
                changeReservedQuantity(anyInt(), anyInt());
    }
}
