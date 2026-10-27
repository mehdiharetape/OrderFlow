package com.example.order_flow.inventory.unit;

import com.example.order_flow.entity.Inventory;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.InventoryNotFoundForProductException;
import com.example.order_flow.exceptions.ProductNotFoundException;
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

@ExtendWith(MockitoExtension.class)
public class GetInventoryByProductIdUnitTest {
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private InventoryService service;

    @Test
    public void getInventoryByProductIdHappyFlow(){
        Integer id = 2008;
        Integer pid = 1002;
        Inventory inventory = new Inventory(id);
        inventory.setProductId(pid);
        inventory.setTotalQuantity(10);
        inventory.setReservedQuantity(3);

        given(productRepository.existsById(pid))
                .willReturn(true);

        given(inventoryRepository.findByProductId(pid)).
                willReturn(Optional.of(inventory));

        var result = service.getInventoryByProductId(pid);

        assertEquals(id, result.getInventoryId());
        assertEquals(pid, result.getProductId());
        assertEquals(7, result.getAvailable());
    }

    @Test //null productId
    public void nullProductIdGetInventoryTest(){
        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.getInventoryByProductId(null));
    }

    @Test //invalid productId
    public void invalidProductIdGetInventoryTest(){
        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.getInventoryByProductId(999));
    }

    @Test //product not found
    public void notFoundedProductIdGetInventoryTest(){
        Integer pid = 2200;
        given(productRepository.existsById(pid))
                .willReturn(false);

        assertThrows(ProductNotFoundException.class,
                () -> service.getInventoryByProductId(pid));
    }

    @Test
    public void inventoryNotFoundTest(){
        Integer pid = 1002;

        given(productRepository.existsById(pid)).willReturn(true);

        given(inventoryRepository.findByProductId(pid)).
                willReturn(Optional.empty());

        assertThrows(InventoryNotFoundForProductException.class,
                () -> service.getInventoryByProductId(pid));
    }
}
