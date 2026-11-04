package com.example.order_flow.order.integration;

import com.example.order_flow.dto.inventory.ReservationChangeDto;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.service.InventoryMovementService;
import com.example.order_flow.service.InventoryService;
import com.example.order_flow.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
public class ExpireDateMovementFailTransactionalTest {
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private OrderService orderService;
    @MockitoSpyBean
    private InventoryMovementService movementService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private InventoryMovementRepository movementRepository;

    @Test
    public void expireOrderShouldRollbackWhenMovementFail(){
        Integer orderId = 3042;
        Integer productId1 = 1003;
        Integer productId2 = 1004;

        int movementCount1 = movementRepository.countByProductId(productId1);
        int movementCount2 = movementRepository.countByProductId(productId2);

        int reserveBefore1 = inventoryRepository.findByProductId(productId1)
                .orElseThrow().getReservedQuantity();
        int reserveBefore2 = inventoryRepository.findByProductId(productId2)
                .orElseThrow().getReservedQuantity();

        doThrow(new RuntimeException("Simulated"))
                .when(movementService).createInventoryMovement(argThat(
                        dto -> dto.getProductId().equals(productId2)
                ));

        //act
        assertThrows(RuntimeException.class,
                () -> orderService.expireOrder(orderId));

        //
        assertEquals(movementCount1, movementRepository.countByProductId(productId1));
        assertEquals(movementCount2, movementRepository.countByProductId(productId2));

        assertEquals(reserveBefore1, inventoryRepository.findByProductId(productId1)
                .orElseThrow().getReservedQuantity());
        assertEquals(reserveBefore2, inventoryRepository.findByProductId(productId2)
                .orElseThrow().getReservedQuantity());

        assertEquals(OrderStatus.PENDING_PAYMENT,
                orderRepository.findById(orderId).orElseThrow().getStatus());
    }
}
