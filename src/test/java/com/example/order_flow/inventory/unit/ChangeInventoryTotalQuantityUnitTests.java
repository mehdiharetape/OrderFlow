package com.example.order_flow.inventory.unit;

import com.example.order_flow.dto.inventory.ChangeTotalQuantityDto;
import com.example.order_flow.dto.movement.CreateMovementDto;
import com.example.order_flow.entity.Inventory;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.InvalidQuantityChangeException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChangeInventoryTotalQuantityUnitTests {
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryMovementService movementService;
    @InjectMocks
    private InventoryService service;

    @Test
    public void changeInventoryTotalQuantityHappyFlow(){
        Integer pid = 1001;
        Integer amount = -5;

        ChangeTotalQuantityDto dto = new ChangeTotalQuantityDto();
        dto.setAmount(amount);

        Inventory inventory = new Inventory();
        inventory.setInventoryId(2001);
        inventory.setProductId(pid);
        inventory.setTotalQuantity(10);
        inventory.setReservedQuantity(3);

        given(inventoryRepository.findByProductId(pid)).
                willReturn(Optional.of(inventory));

        when(movementService.createInventoryMovement(any(CreateMovementDto.class)))
                .thenReturn(5001);

        //act
        InventoryResult result = service.changeTotalQuantity(pid, dto);

        assertEquals(2001 , result.inventory());
        assertEquals(5001, result.inventoryMovement());


        verify(inventoryRepository).changeTotalQuantity(2001, 5);
    }

    @Test //invalid amount
    public void InvalidChangeAmountTest(){
        Integer pid = 1001;
        Integer amount = -8;

        ChangeTotalQuantityDto dto = new ChangeTotalQuantityDto();
        dto.setAmount(amount);

        Inventory inventory = new Inventory();
        inventory.setInventoryId(2001);
        inventory.setProductId(pid);
        inventory.setTotalQuantity(10);
        inventory.setReservedQuantity(3);

        given(inventoryRepository.findByProductId(pid)).
                willReturn(Optional.of(inventory));

        assertThrows(InvalidQuantityChangeException.class,
                () -> service.changeTotalQuantity(pid, dto));


        verify(inventoryRepository, never()).changeTotalQuantity(anyInt(), anyInt());
    }

    @Test //null inventoryId
    public void nullInventoryIdTest(){
        ChangeTotalQuantityDto dto = new ChangeTotalQuantityDto();
        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.changeTotalQuantity(null, dto));
    }

    @Test //invalid inventoryId
    public void invalidIProductIdTest(){
        ChangeTotalQuantityDto dto = new ChangeTotalQuantityDto();

        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.changeTotalQuantity(999, dto));
    }

    @Test //inventory not found
    public void inventoryNotFoundForThisProductTest(){
        given(inventoryRepository.findByProductId(200000)).
                willReturn(Optional.empty());

        assertThrows(InventoryNotFoundForProductException.class,
                () -> service.changeTotalQuantity(200000,
                        any(ChangeTotalQuantityDto.class)));
    }
}
