package com.example.order_flow.inventory.integration;

import com.example.order_flow.dto.inventory.ChangeTotalQuantityDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.exceptions.InvalidQuantityChangeException;
import com.example.order_flow.exceptions.InventoryNotFoundException;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.service.InventoryService;
import com.example.order_flow.utils.InventoryResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ChangeQuantityInventoryIntegrationTest {
    @Autowired
    private InventoryService service;
    @Autowired
    private InventoryMovementRepository movementRepository;
    @Autowired
    private InventoryRepository inventoryRepository;

    //@Test
    public void changeQuantityInventoryHappyFlow(){
        ChangeTotalQuantityDto dto = new ChangeTotalQuantityDto();
        dto.setAmount(-2);
        Integer iid = 2008;

        InventoryResult result = service.changeTotalQuantity(iid, dto);
        Optional<InventoryMovement> movement = movementRepository.findById(result.inventoryMovement());

        assertNotNull(result);
        assertEquals(2008, result.inventory());
        assertEquals(result.inventoryMovement(), movement.get().getInventoryMovementId());
    }

    @Test
    public void changeInvalidQuantityInventory(){
        ChangeTotalQuantityDto dto = new ChangeTotalQuantityDto();
        dto.setAmount(-9);
        Integer iid = 2009;

        assertThrows(InvalidQuantityChangeException.class,
                () -> service.changeTotalQuantity(iid, dto));
    }

    @Test
    public void inventoryNotFoundTest(){
        Integer id = 200000;
        ChangeTotalQuantityDto dto = new ChangeTotalQuantityDto();

        assertThrows(InventoryNotFoundException.class,
                () -> service.changeTotalQuantity(id, dto));
    }
}
