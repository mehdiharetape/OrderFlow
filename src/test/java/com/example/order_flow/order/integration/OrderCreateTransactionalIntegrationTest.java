package com.example.order_flow.order.integration;

import com.example.order_flow.dto.order.OrderItemRequestDto;
import com.example.order_flow.entity.OrderItem;
import com.example.order_flow.repository.OrderItemRepository;
import com.example.order_flow.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class OrderCreateTransactionalIntegrationTest {
    @MockitoSpyBean
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderService service;

    @Test
    public void orderShouldRollbackWhenOrderItemSaveFailure(){
        doThrow(new RuntimeException("Simulated Exception"))
                .when(orderItemRepository).save(any(OrderItem.class));

        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1003);
        oi2.setQuantity(2);

        RuntimeException exception = assertThrows(RuntimeException.class,
                ()->service.createOrder(List.of(oi1, oi2)));
        assertEquals("Simulated Exception", exception.getMessage());

        verify(orderItemRepository).save(any(OrderItem.class));
    }
}
