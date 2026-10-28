package com.example.order_flow.inventory.integration;

import com.example.order_flow.dto.inventory.ChangeTotalQuantityDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.exceptions.InvalidQuantityChangeException;
import com.example.order_flow.exceptions.InventoryNotFoundForProductException;
import com.example.order_flow.repository.InventoryMovementRepository;
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

    //@Test
    public void changeQuantityInventoryHappyFlow(){
        ChangeTotalQuantityDto dto = new ChangeTotalQuantityDto();
        dto.setAmount(-2);
        Integer pid = 1003;

        InventoryResult result = service.changeTotalQuantity(pid, dto);
        Optional<InventoryMovement> movement = movementRepository.findById(result.inventoryMovement());

        assertNotNull(result);
        assertEquals(2010, result.inventory());
        assertEquals(result.inventoryMovement(), movement.get().getInventoryMovementId());
    }

    //@Test
    public void changeInvalidQuantityInventory(){
        ChangeTotalQuantityDto dto = new ChangeTotalQuantityDto();
        dto.setAmount(-9);
        Integer pid = 1003;

        assertThrows(InvalidQuantityChangeException.class,
                () -> service.changeTotalQuantity(pid, dto));
    }

    @Test
    public void inventoryNotFoundTest(){
        Integer pid = 200000;
        ChangeTotalQuantityDto dto = new ChangeTotalQuantityDto();

        assertThrows(InventoryNotFoundForProductException.class,
                () -> service.changeTotalQuantity(pid, dto));
    }
}
