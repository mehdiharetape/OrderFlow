package com.example.order_flow.inventory.unit;

import com.example.order_flow.dto.inventory.ChangeTotalQuantityDto;
import com.example.order_flow.entity.Inventory;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.InvalidQuantityChangeException;
import com.example.order_flow.exceptions.InventoryNotFoundException;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.InventoryRepository;
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
    private InventoryMovementRepository movementRepository;
    @InjectMocks
    private InventoryService service;

    @Test
    public void changeInventoryTotalQuantityHappyFlow(){
        Integer id = 2008;
        Integer amount = -5;

        ChangeTotalQuantityDto dto = new ChangeTotalQuantityDto();
        dto.setAmount(amount);

        Inventory inventory = new Inventory(id);
        inventory.setProductId(1002);
        inventory.setTotalQuantity(10);
        inventory.setReservedQuantity(3);

        given(inventoryRepository.findById(id)).
                willReturn(Optional.of(inventory));

        when(movementRepository.save(any(InventoryMovement.class)))
                .thenAnswer(invocation ->{
                    InventoryMovement saved = invocation.getArgument(0);
                    saved.setInventoryMovementId(5001);
                    return saved;
                });

        //act
        InventoryResult result = service.changeTotalQuantity(id, dto);

        assertEquals(id, result.inventory());
        assertEquals(5001, result.inventoryMovement());


        verify(inventoryRepository).changeTotalQuantity(id, 5);
        verify(movementRepository).save(any(InventoryMovement.class));
    }

    @Test //invalid amount
    public void InvalidChangeAmountTest(){
        Integer id = 2008;
        Integer amount = -8;

        ChangeTotalQuantityDto dto = new ChangeTotalQuantityDto();
        dto.setAmount(amount);

        Inventory inventory = new Inventory(id);
        inventory.setProductId(1002);
        inventory.setTotalQuantity(10);
        inventory.setReservedQuantity(3);

        given(inventoryRepository.findById(id)).
                willReturn(Optional.of(inventory));

        assertThrows(InvalidQuantityChangeException.class,
                () -> service.changeTotalQuantity(id, dto));


        verify(inventoryRepository, never()).changeTotalQuantity(anyInt(), anyInt());
        verify(movementRepository, never()).save(any(InventoryMovement.class));
    }

    @Test //null inventoryId
    public void nullInventoryIdTest(){
        ChangeTotalQuantityDto dto = new ChangeTotalQuantityDto();
        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.changeTotalQuantity(null, dto));
    }

    @Test //invalid inventoryId
    public void invalidInventoryIdTest(){
        ChangeTotalQuantityDto dto = new ChangeTotalQuantityDto();

        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.changeTotalQuantity(999, dto));
    }

    @Test //inventory not found
    public void inventoryNotFoundTest(){
        given(inventoryRepository.findById(200000)).
                willReturn(Optional.empty());

        assertThrows(InventoryNotFoundException.class,
                () -> service.changeTotalQuantity(200000,
                        any(ChangeTotalQuantityDto.class)));
    }
}
