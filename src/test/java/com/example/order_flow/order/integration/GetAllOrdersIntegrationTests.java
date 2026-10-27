package com.example.order_flow.order.integration;

import com.example.order_flow.dto.order.OrderBriefResponseDto;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GetAllOrdersIntegrationTests {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;

    @Test
    public void getAllOrdersIntegrationTest(){
        List<OrderBriefResponseDto> result = orderService.getAllOrders();

        assertEquals(3, result.size());
        assertEquals(3001, result.get(0).getOrderId());
        assertEquals(3018, result.get(1).getOrderId());
        assertEquals(BigDecimal.valueOf(290.46), result.get(0).getTotalAmount());
        assertEquals(BigDecimal.valueOf(171.82), result.get(1).getTotalAmount());
    }
}
