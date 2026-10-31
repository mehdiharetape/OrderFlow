package com.example.order_flow.order.integration;

import com.example.order_flow.dto.inventory.ReservationChangeDto;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.service.InventoryService;
import com.example.order_flow.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
public class CancelInventoryFailCancelOrderTransactionalIntegrationTest {
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private InventoryMovementRepository movementRepository;
    @Autowired
    private OrderRepository orderRepository;
    @MockitoSpyBean
    private InventoryService inventoryService;

    @Test
    public void cancelReservationInventoryFailWhenOrderCancel(){
        Integer orderId = 3031;
        Integer productId1 = 1001;
        Integer productId2 = 1004;

        int movementCount1 = movementRepository.countByProductId(productId1);
        int movementCount2 = movementRepository.countByProductId(productId2);

        int reserveBefore1 = inventoryRepository.findByProductId(productId1)
                .orElseThrow().getReservedQuantity();
        int reserveBefore2 = inventoryRepository.findByProductId(productId2)
                .orElseThrow().getReservedQuantity();

        //fail when the second product is being released.
        doThrow(new RuntimeException("Simulated Exception"))
                .when(inventoryService)
                .cancelReservation(eq(productId2), any(ReservationChangeDto.class));

        //act & asserts
        assertThrows(RuntimeException.class,
                () -> orderService.cancelOrder(orderId));


        assertEquals(reserveBefore1, inventoryRepository.findByProductId(productId1)
                .orElseThrow().getReservedQuantity());
        assertEquals(reserveBefore2, inventoryRepository.findByProductId(productId2)
                .orElseThrow().getReservedQuantity());

        assertEquals(movementCount1,
                movementRepository.countByProductId(productId1));
        assertEquals(movementCount2,
                movementRepository.countByProductId(productId2));

        assertEquals(OrderStatus.PENDING_PAYMENT,
                orderRepository.findById(orderId).orElseThrow().getStatus());
    }
}
