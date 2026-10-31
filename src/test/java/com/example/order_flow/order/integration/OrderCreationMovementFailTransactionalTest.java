package com.example.order_flow.order.integration;

import com.example.order_flow.dto.order.OrderItemRequestDto;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.service.InventoryMovementService;
import com.example.order_flow.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
public class OrderCreationMovementFailTransactionalTest {
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private InventoryMovementRepository movementRepository;
    @Autowired
    private OrderService service;
    @MockitoSpyBean
    private InventoryMovementService movementService;

    @Test
    public void orderShouldRollbackWhenInventoryReservationFailure(){
        Integer productId1 = 1001;
        Integer productId2 = 1004;
        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(productId1);
        oi1.setQuantity(2);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(productId2);
        oi2.setQuantity(2);

        int movementCount1 = movementRepository.countByProductId(productId1);
        int movementCount2 = movementRepository.countByProductId(productId2);

        int reserveBefore1 = inventoryRepository.findByProductId(productId1)
                .orElseThrow().getReservedQuantity();
        int reserveBefore2 = inventoryRepository.findByProductId(productId2)
                .orElseThrow().getReservedQuantity();

        doThrow(new RuntimeException("Simulated Exception"))
                .when(movementService)
                .createInventoryMovement(argThat(dto ->
                        dto.getProductId().equals(productId2)));

        assertThrows(RuntimeException.class,
                ()-> service.createOrder(List.of(oi1, oi2)));

        assertEquals(reserveBefore1, inventoryRepository.findByProductId(productId1)
                .orElseThrow().getReservedQuantity());
        assertEquals(reserveBefore2, inventoryRepository.findByProductId(productId2)
                .orElseThrow().getReservedQuantity());

        assertEquals(movementCount1, movementRepository.countByProductId(productId1));
        assertEquals(movementCount2, movementRepository.countByProductId(productId2));

    }
}
