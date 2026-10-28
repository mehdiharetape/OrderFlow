package com.example.order_flow.inventory.unit;

import com.example.order_flow.dto.inventory.ReservationChangeDto;
import com.example.order_flow.dto.movement.CreateMovementDto;
import com.example.order_flow.entity.Inventory;
import com.example.order_flow.entity.enums.MovementReason;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.InvalidReservationAmountException;
import com.example.order_flow.exceptions.InventoryNotFoundForProductException;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.service.InventoryMovementService;
import com.example.order_flow.service.InventoryService;
import com.example.order_flow.utils.InventoryResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class CancelReserveInventoryUnitTests {
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryMovementService movementService;
    @InjectMocks
    private InventoryService service;

    @Test
    public void cancelReserveInventoryHappyFlow(){
        Integer pid = 1001;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setReserveAmount(5);

        Inventory inventory = new Inventory(2009);
        inventory.setProductId(pid);
        inventory.setTotalQuantity(15);
        inventory.setReservedQuantity(9);

        given(inventoryRepository.findByProductId(pid))
                .willReturn(Optional.of(inventory));

        when(movementService.createInventoryMovement(any(CreateMovementDto.class)))
                .thenReturn(5014);

        //act
        InventoryResult result = service.cancelReservation(pid, dto);

        assertNotNull(result);
        assertEquals(4, inventory.getReservedQuantity() - 5);
        assertEquals(2009, result.inventory());
        assertEquals(5014, result.inventoryMovement());

        verify(inventoryRepository).findByProductId(pid);
        verify(inventoryRepository).changeReservedQuantity(anyInt(), anyInt());
    }

    //zero or negative cancel amount
    @Test
    public void zeroCancelReserveAmountTest(){
        Integer pid = 1001;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setReserveAmount(0);

        Inventory inventory = new Inventory(2009);
        inventory.setProductId(pid);
        inventory.setTotalQuantity(15);
        inventory.setReservedQuantity(9);

        given(inventoryRepository.findByProductId(pid))
                .willReturn(Optional.of(inventory));

        //act
        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.reservation(pid, dto));

        verify(inventoryRepository).findByProductId(pid);
        verify(inventoryRepository, never()).changeReservedQuantity(anyInt(), anyInt());
    }

    @Test
    public void negativeCancelReserveAmountTest(){
        Integer pid = 1001;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setReserveAmount(-1);

        Inventory inventory = new Inventory(2009);
        inventory.setProductId(pid);
        inventory.setTotalQuantity(15);
        inventory.setReservedQuantity(9);

        given(inventoryRepository.findByProductId(pid))
                .willReturn(Optional.of(inventory));

        //act
        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.reservation(pid, dto));

        verify(inventoryRepository).findByProductId(pid);
        verify(inventoryRepository, never()).changeReservedQuantity(anyInt(), anyInt());
    }

    //invalid reservation amount
    @Test
    public void invalidCancelReserveAmountTest(){
        Integer pid = 1001;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setReserveAmount(10);

        Inventory inventory = new Inventory(2009);
        inventory.setProductId(pid);
        inventory.setTotalQuantity(15);
        inventory.setReservedQuantity(9);

        given(inventoryRepository.findByProductId(pid))
                .willReturn(Optional.of(inventory));

        //act
        assertThrows(InvalidReservationAmountException.class,
                () -> service.reservation(pid, dto));

        verify(inventoryRepository).findByProductId(pid);
        verify(inventoryRepository, never()).changeReservedQuantity(anyInt(), anyInt());
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
