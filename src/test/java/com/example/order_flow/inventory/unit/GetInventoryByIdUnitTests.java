package com.example.order_flow.inventory.unit;

import com.example.order_flow.dto.inventory.InventoryDetailResponseDto;
import com.example.order_flow.entity.Inventory;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.InventoryNotFoundException;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GetInventoryByIdUnitTests {
    @Mock
    private InventoryRepository inventoryRepository;
    @InjectMocks
    private InventoryService service;

    @Test
    public void getInventoryByIdHappyFlow(){
        Integer id = 2004;
        Inventory inventory = new Inventory(id);
        inventory.setProductId(1001);
        inventory.setTotalQuantity(10);
        inventory.setReservedQuantity(3);

        given(inventoryRepository.findById(2004)).
                willReturn(Optional.of(inventory));

        InventoryDetailResponseDto result =
                service.getInventoryById(id);

        assertEquals(id, result.getInventoryId());
        assertEquals(10, result.getTotalQuantity());
        assertEquals(3, result.getReservedQuantity());
        assertEquals(1001, result.getProductId());
        assertEquals(7, result.getAvailable());
    }

    @Test //null id
    public void nullInventoryIdTest(){
        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.getInventoryById(null));

        verify(inventoryRepository, never()).findById(null);
    }

    @Test //null id
    public void invalidInventoryIdTest(){
        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.getInventoryById(1999));

        verify(inventoryRepository, never()).findById(1999);
    }

    @Test //inventory not found
    public void inventoryNotFoundTest(){
        given(inventoryRepository.findById(2007))
                .willReturn(Optional.empty());

        assertThrows(InventoryNotFoundException.class,
                () -> service.getInventoryById(2007));
    }
}
