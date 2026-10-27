package com.example.order_flow.order.integration;

import com.example.order_flow.exceptions.OrderNotFoundException;
import com.example.order_flow.repository.OrderItemRepository;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.service.OrderService;
import com.example.order_flow.utils.OrderResultById;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GetOrderDetailsByOrderIdIntegrationTest {
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;

    //happy flow
    @Test
    public void getOrderDetailsByOrderIdHappyFlow(){
        Integer orderId = 3001;

        OrderResultById result = orderService.getOrderDetailById(orderId);

        assertNotNull(result);
        assertNotNull(result.order());
        assertNotNull(result.orderItems());

        assertEquals(orderId, result.order().getOrderId());

        assertEquals(2, result.orderItems().size());
        assertEquals(orderId, result.orderItems().get(0).getOrderId());
        assertEquals(1001, result.orderItems().get(0).getProductId());
        assertEquals(3, result.orderItems().get(0).getQuantity());

        assertEquals(orderId, result.orderItems().get(1).getOrderId());
        assertEquals(1002, result.orderItems().get(1).getProductId());
        assertEquals(2, result.orderItems().get(1).getQuantity());
    }

    //empty order item list
    @Test
    public void emptyOrderItemsListTest(){
        Integer orderId = 3019;

        OrderResultById result = orderService.getOrderDetailById(orderId);

        assertNotNull(result);
        assertNotNull(result.order());
        assertNotNull(result.orderItems());
        assertTrue(result.orderItems().isEmpty());

        assertEquals(orderId, result.order().getOrderId());
    }

    //order not found
    @Test
    public void notFoundOrderTest(){
        Integer orderId = 3100;

        assertThrows(OrderNotFoundException.class,
                () -> orderService.getOrderDetailById(3100));
    }
}
