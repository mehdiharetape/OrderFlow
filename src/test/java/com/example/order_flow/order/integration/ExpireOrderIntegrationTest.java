package com.example.order_flow.order.integration;

import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.OrderItem;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.exceptions.OrderNotFoundException;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.repository.OrderItemRepository;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ExpireOrderIntegrationTest {
    @Autowired
    private OrderService service;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private InventoryRepository inventoryRepository;

    //@Test
    public void expireOrderHappyFlow(){
        Integer orderId = 3042;

        List<OrderItem> items = StreamSupport.stream(orderItemRepository
                        .findAllByOrderId(orderId).spliterator(), false).toList();

        int itemQuantity1 = items.get(0).getQuantity();
        int itemQuantity2 = items.get(1).getQuantity();

        int reservedQuantity1 = inventoryRepository.findByProductId(items.get(0).getProductId())
                .orElseThrow().getReservedQuantity();
        int reservedQuantity2 = inventoryRepository.findByProductId(items.get(1).getProductId())
                .orElseThrow().getReservedQuantity();

        service.expireOrder(orderId);

        Order order = orderRepository.findById(orderId).orElseThrow();
        assertEquals(OrderStatus.EXPIRED, order.getStatus());

        int reservedAfterQuantity1 = inventoryRepository.findByProductId(items.get(0).getProductId())
                .orElseThrow().getReservedQuantity();
        int reservedAfterQuantity2 = inventoryRepository.findByProductId(items.get(1).getProductId())
                .orElseThrow().getReservedQuantity();

        assertEquals(reservedAfterQuantity1, reservedQuantity1 - itemQuantity1);
        assertEquals(reservedAfterQuantity2, reservedQuantity2 - itemQuantity2);
    }

    @Test
    public void orderNotFoundTest(){
        Integer orderId = 3200;

        assertThrows(OrderNotFoundException.class,
                () -> service.expireOrder(orderId));


    }
}
