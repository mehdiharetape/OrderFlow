package com.example.order_flow.order.unit;

import com.example.order_flow.dto.order.OrderBriefResponseDto;
import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GetAllOrdersUnitTests {
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderService orderService;

    @Test
    public void getAllOrdersHappyFlow(){
        Order order1 = new Order(3001);
        order1.setStatus(OrderStatus.PENDING_PAYMENT);
        order1.setTotalAmount(BigDecimal.valueOf(30.15));
        Order order2 = new Order(3002);
        order2.setStatus(OrderStatus.PENDING_PAYMENT);
        order2.setTotalAmount(BigDecimal.valueOf(20.75));

        given(orderRepository.findAll())
                .willReturn(List.of(order1, order2));

        List<OrderBriefResponseDto> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        assertEquals(3001, result.get(0).getOrderId());
        assertEquals(3002, result.get(1).getOrderId());
        assertEquals(BigDecimal.valueOf(30.15), result.get(0).getTotalAmount());
        assertEquals(BigDecimal.valueOf(20.75), result.get(1).getTotalAmount());

        verify(orderRepository).findAll();
    }

    @Test
    public void emptyOrderListTest(){
        given(orderRepository.findAll())
                .willReturn(List.of());

        List<OrderBriefResponseDto> result = orderService.getAllOrders();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
