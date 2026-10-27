package com.example.order_flow.inventory.unit;

import com.example.order_flow.dto.inventory.CreateInventoryDto;
import com.example.order_flow.entity.Inventory;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.InventoryProductExistException;
import com.example.order_flow.exceptions.ProductNotFoundException;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.InventoryService;
import com.example.order_flow.utils.InventoryResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateInventoryUnitTests {
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryMovementRepository movementRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private InventoryService service;

    @Test //when total quantity > 0
    public void createInitialInventoryHappyFlow(){
        //Arrange
        CreateInventoryDto dto = new CreateInventoryDto();
        dto.setProductId(1001);
        dto.setTotalQuantity(10);

        when(productRepository.existsById(1001)).thenReturn(true);
        when(inventoryRepository.existsByProductId(1001)).thenReturn(false);

        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> {
                    Inventory save = invocation.getArgument(0);
                    save.setInventoryId(2000);
                    return save;
                });

        when(movementRepository.save(any(InventoryMovement.class)))
                .thenAnswer(invocation -> {
                    InventoryMovement saved = new InventoryMovement(5000);
                    return saved;
                });

        //Act
        InventoryResult result = service.createInventory(dto);

        //Assert
        assertNotNull(result);
        assertEquals(2000, result.inventory());
        assertEquals(5000, result.inventoryMovement());

        verify(productRepository).existsById(1001);
        verify(inventoryRepository).save(any(Inventory.class));
        verify(movementRepository).save(any(InventoryMovement.class));
    }

    @Test //when total quantity=0
    public void createInitialInventoryZeroTotalQuantityHappyFlow(){
        //Arrange
        CreateInventoryDto dto = new CreateInventoryDto();
        dto.setProductId(1001);
        dto.setTotalQuantity(0);

        when(productRepository.existsById(1001)).thenReturn(true);
        when(inventoryRepository.existsByProductId(1001)).thenReturn(false);

        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> {
                    Inventory save = invocation.getArgument(0);
                    save.setInventoryId(2000);
                    return save;
                });

        //Act
        InventoryResult result = service.createInventory(dto);

        assertNotNull(result);
        assertEquals(2000, result.inventory());
        assertNull(result.inventoryMovement());

        verify(productRepository).existsById(1001);
        verify(inventoryRepository).save(any(Inventory.class));
        verify(movementRepository, never()).save(any(InventoryMovement.class));
    }

    @Test //negative product id
    public void negativeProductIdTest(){
        //Arrange
        CreateInventoryDto dto = new CreateInventoryDto();
        dto.setProductId(-1);
        dto.setTotalQuantity(10);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.createInventory(dto));

        verify(productRepository, never()).existsById(1001);
        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(movementRepository, never()).save(any(InventoryMovement.class));
    }

    @Test //invalid product id
    public void invalidProductIdTest(){
        //Arrange
        CreateInventoryDto dto = new CreateInventoryDto();
        dto.setProductId(999);
        dto.setTotalQuantity(10);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.createInventory(dto));

        verify(productRepository, never()).existsById(1001);
        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(movementRepository, never()).save(any(InventoryMovement.class));
    }

    @Test //negative product id
    public void negativeTotalQuantityTest(){
        //Arrange
        CreateInventoryDto dto = new CreateInventoryDto();
        dto.setProductId(1001);
        dto.setTotalQuantity(-1);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.createInventory(dto));

        verify(productRepository, never()).existsById(1001);
        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(movementRepository, never()).save(any(InventoryMovement.class));
    }

    @Test //product id is valid but not found
    public void productNotFoundTest(){
        //Arrange
        CreateInventoryDto dto = new CreateInventoryDto();
        dto.setProductId(99999);
        dto.setTotalQuantity(10);

        when(productRepository.existsById(99999)).thenReturn(false);

        assertThrows(ProductNotFoundException.class,
                () -> service.createInventory(dto));

        verify(productRepository).existsById(99999);
        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(movementRepository, never()).save(any(InventoryMovement.class));
    }

    @Test //repetitive product id
    public void repetitiveProductIdTest(){
        //Arrange
        CreateInventoryDto dto = new CreateInventoryDto();
        dto.setProductId(1001);
        dto.setTotalQuantity(10);

        when(productRepository.existsById(1001)).thenReturn(true);
        when(inventoryRepository.existsByProductId(1001)).thenReturn(true);

        //Act
        assertThrows(InventoryProductExistException.class,
                () -> service.createInventory(dto));


        verify(productRepository).existsById(1001);
        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(movementRepository, never()).save(any(InventoryMovement.class));
    }
}
