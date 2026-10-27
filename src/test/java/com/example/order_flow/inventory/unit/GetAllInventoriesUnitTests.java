package com.example.order_flow.inventory.unit;

import com.example.order_flow.dto.inventory.InventoryDetailResponseDto;
import com.example.order_flow.entity.Inventory;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GetAllInventoriesUnitTests {
    @Mock
    private InventoryRepository inventoryRepository;
    @InjectMocks
    private InventoryService service;

    @Test
    public void getAllInventoriesHappyFlow(){
        Inventory i1 = new Inventory(2004);
        i1.setTotalQuantity(10);
        i1.setReservedQuantity(1);
        i1.setProductId(1002);

        Inventory i2 = new Inventory(2005);
        i2.setTotalQuantity(11);
        i2.setReservedQuantity(2);
        i2.setProductId(1003);

        given(inventoryRepository.findAll()).
                willReturn(List.of(i1, i2));

        List<InventoryDetailResponseDto> result = service.getAllInventories();

        assertEquals(2, result.size());
        assertEquals(2004, result.get(0).getInventoryId());
        assertEquals(2005, result.get(1).getInventoryId());
    }

    @Test
    public void emptyInventoriesTest(){
        given(inventoryRepository.findAll())
                .willReturn(List.of());

        List<InventoryDetailResponseDto> result = service.getAllInventories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
