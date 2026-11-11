package com.example.order_flow.inventory.unit;

import com.example.order_flow.dto.inventory.ReservationChangeDto;
import com.example.order_flow.dto.movement.CreateMovementDto;
import com.example.order_flow.entity.Inventory;
import com.example.order_flow.entity.enums.MovementReason;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.InvalidReservationAmountException;
import com.example.order_flow.exceptions.InventoryFailException;
import com.example.order_flow.exceptions.InventoryNotFoundForProductException;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.InventoryMovementService;
import com.example.order_flow.service.InventoryService;
import com.example.order_flow.utils.records.InventoryResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    @Mock
    private ProductRepository productRepository;

    private InventoryService service;
    private final Clock clock = Clock.fixed(
            Instant.parse("2026-11-01T10:00:00Z"),
            ZoneOffset.UTC
    );

    @BeforeEach
    public void setUp(){
        service = new InventoryService(inventoryRepository,
                productRepository, movementService, clock);
    }

    @Test
    public void cancelReserveInventoryHappyFlow(){
        Integer pid = 1001;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setOrderId(3001);
        dto.setReserveAmount(5);

        Inventory inventory = new Inventory(2009);
        inventory.setProductId(pid);
        inventory.setTotalQuantity(15);
        inventory.setReservedQuantity(9);

        given(inventoryRepository.findByProductId(pid))
                .willReturn(Optional.of(inventory));

        when(movementService.createInventoryMovement(any(CreateMovementDto.class)))
                .thenReturn(5014);

        when(inventoryRepository.changeReservedQuantity(2009, 4))
                .thenReturn(1);

        //act
        InventoryResult result = service.cancelReservation(pid, dto);

        assertNotNull(result);
        assertEquals(4, inventory.getReservedQuantity() - 5);
        assertEquals(2009, result.inventory());
        assertEquals(5014, result.inventoryMovement());

        verify(inventoryRepository).findByProductId(pid);
        verify(inventoryRepository).changeReservedQuantity(anyInt(), anyInt());

        ArgumentCaptor<CreateMovementDto> captor =
                ArgumentCaptor.forClass(CreateMovementDto.class);

        verify(movementService).createInventoryMovement(captor.capture());
        assertEquals(3001, captor.getValue().getOrderId());
        assertEquals(pid, captor.getValue().getProductId());
        assertEquals(0, captor.getValue().getPhysicalChange());
        assertEquals(-5, captor.getValue().getReservedChange());
        assertEquals(LocalDateTime.now(clock), captor.getValue().getCreatedAt());
        assertEquals(MovementReason.CANCEL_RESERVATION, captor.getValue().getReason());
    }

    //when raw not update in db
    @Test
    public void updateInventoryFailTest(){
        Integer pid = 1001;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setOrderId(3001);
        dto.setReserveAmount(3);

        Inventory inventory = new Inventory(2008);
        inventory.setProductId(pid);
        inventory.setTotalQuantity(10);
        inventory.setReservedQuantity(5);

        given(inventoryRepository.findByProductId(pid)).
                willReturn(Optional.of(inventory));
        when(inventoryRepository.changeReservedQuantity(2008, 2))
                .thenReturn(0);

        //act
        assertThrows(InventoryFailException.class,
                () -> service.cancelReservation(pid, dto));


        verify(inventoryRepository).findByProductId(pid);
        verify(inventoryRepository).
                changeReservedQuantity(anyInt(), anyInt());
        verify(movementService, never()).createInventoryMovement(
                any(CreateMovementDto.class));
    }

    //zero or negative cancel amount
    @Test
    public void zeroCancelReserveAmountTest(){
        Integer pid = 1001;
        ReservationChangeDto dto = new ReservationChangeDto();
        dto.setOrderId(3001);
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
        dto.setOrderId(3001);
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
        dto.setOrderId(3001);
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
