package com.example.order_flow.orderitem.integration;

import com.example.order_flow.dto.order.OrderItemResponseDto;
import com.example.order_flow.repository.OrderItemRepository;
import com.example.order_flow.service.OrderItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GetAllOrderItemsByOrderIdIntegrationTests {
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderItemService orderItemService;

    @Test
    public void getALlOrderItemsByOrderIdHappyFlow(){
        List<OrderItemResponseDto> result =
                orderItemService.getAllOrderItemsByOrderId(3018);


        assertEquals(3018, result.get(0).getOrderId());
        assertEquals(3018, result.get(1).getOrderId());

        assertEquals(1001, result.get(0).getProductId());
        assertEquals(1002, result.get(1).getProductId());
    }

    @Test
    public void emptyOrderItemsByOrderId(){
        List<OrderItemResponseDto> result =
                orderItemService.getAllOrderItemsByOrderId(3100);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
