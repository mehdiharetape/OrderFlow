package com.example.order_flow.order.integration;

import com.example.order_flow.dto.order.OrderBriefResponseDto;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GetOrdersByOrderStatusIntegrationTests {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;

    @Test
    public void getOrdersByOrderStatusHappyFlow(){
        List<OrderBriefResponseDto> result =
                orderService.getOrderByStatus(OrderStatus.PENDING_PAYMENT);

        assertEquals(2, result.size());
        assertEquals(OrderStatus.PENDING_PAYMENT, result.get(0).getStatus());
        assertEquals(OrderStatus.PENDING_PAYMENT, result.get(1).getStatus());

        assertEquals(3001, result.get(0).getOrderId());
        assertEquals(3018, result.get(1).getOrderId());
    }

    @Test
    public void emptyOrdersListTest(){
        List<OrderBriefResponseDto> result =
                orderService.getOrderByStatus(OrderStatus.SHIPPED);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
