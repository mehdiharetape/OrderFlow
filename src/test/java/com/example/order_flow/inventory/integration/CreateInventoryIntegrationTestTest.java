package com.example.order_flow.inventory.integration;

import com.example.order_flow.dto.inventory.CreateInventoryDto;
import com.example.order_flow.entity.Inventory;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.entity.enums.MovementReason;
import com.example.order_flow.exceptions.InventoryProductExistException;
import com.example.order_flow.exceptions.ProductExistsException;
import com.example.order_flow.exceptions.ProductNotFoundException;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.InventoryService;
import com.example.order_flow.utils.InventoryResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

@SpringBootTest
public class CreateInventoryIntegrationTestTest {
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private InventoryMovementRepository movementRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryService service;

    //@Test
    public void createInventoryHappyFlow(){
        CreateInventoryDto dto = new CreateInventoryDto();
        dto.setProductId(1001);
        dto.setTotalQuantity(10);

        InventoryResult result = service.createInventory(dto);
        Optional<Inventory> inventory =
                inventoryRepository.findById(result.inventory());
        Optional<InventoryMovement> movement =
                movementRepository.findById(result.inventoryMovement());

        assertNotNull(result);
        assertTrue(inventory.isPresent());
        assertTrue(movement.isPresent());

        assertEquals(result.inventory(), inventory.get().getInventoryId());
        assertEquals(result.inventoryMovement(), movement.get().getInventoryMovementId());

        assertEquals(0, inventory.get().getReservedQuantity());
        assertEquals(MovementReason.INITIAL_STOCK, movement.get().getReason());
    }

    //@Test
    public void createInventoryZeroQuantityHappyFlow(){
        CreateInventoryDto dto = new CreateInventoryDto();
        dto.setProductId(1002);
        dto.setTotalQuantity(0);

        InventoryResult result = service.createInventory(dto);
        Optional<Inventory> inventory =
                inventoryRepository.findById(result.inventory());

        assertNotNull(result);
        assertTrue(inventory.isPresent());
        assertNull(result.inventoryMovement());

        assertEquals(result.inventory(), inventory.get().getInventoryId());

        assertEquals(0, inventory.get().getReservedQuantity());
    }

    //@Test
    public void productNotFoundTest(){
        CreateInventoryDto dto = new CreateInventoryDto();
        dto.setProductId(100000);
        dto.setTotalQuantity(10);

        assertThrows(ProductNotFoundException.class,
                () -> service.createInventory(dto));
    }

    @Test //repetitive product id in inventory creation
    public void createRepetitiveProductInventory(){
        CreateInventoryDto dto = new CreateInventoryDto();
        dto.setProductId(1001); //this product id already exists in inventory
        dto.setTotalQuantity(10);

        assertThrows(InventoryProductExistException.class,
                () -> service.createInventory(dto));
    }

}
