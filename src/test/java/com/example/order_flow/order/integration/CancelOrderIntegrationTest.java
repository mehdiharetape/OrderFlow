package com.example.order_flow.order.integration;

import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.exceptions.OrderCancelFailException;
import com.example.order_flow.exceptions.OrderNotFoundException;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class CancelOrderIntegrationTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

    //@Test
    public void orderCancelHappyFlow(){
        Integer orderId = 3023;

        Integer id = orderService.cancelOrder(orderId);
        Optional<Order> order = orderRepository.findById(orderId);

        assertEquals(orderId, id);
        assertEquals(OrderStatus.CANCELED, order.get().getStatus());
    }

    //wrong order status
    //@Test
    public void wrongOrderStatusCancelTest(){
        Integer orderId = 3019;

        assertThrows(OrderCancelFailException.class,
                () -> orderService.cancelOrder(orderId));

        Optional<Order> order = orderRepository.findById(orderId);
        assertEquals(OrderStatus.PAID, order.get().getStatus());
    }

    //empty order items
    //@Test
    public void emptyOrderItemListCancelTest(){
        Integer orderId = 3019;

        assertThrows(OrderCancelFailException.class,
                () -> orderService.cancelOrder(orderId));

        Optional<Order> order = orderRepository.findById(orderId);
        assertEquals(OrderStatus.PENDING_PAYMENT, order.get().getStatus());
    }

    //order not found
    @Test
    public void orderNotFoundCancelOrderTest(){
        Integer orderId = 3900;

        assertThrows(OrderNotFoundException.class,
                () -> orderService.cancelOrder(orderId));
    }
}
