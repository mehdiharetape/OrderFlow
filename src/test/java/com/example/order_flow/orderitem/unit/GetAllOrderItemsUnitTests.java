package com.example.order_flow.orderitem.unit;

import com.example.order_flow.dto.order.OrderItemResponseDto;
import com.example.order_flow.entity.OrderItem;
import com.example.order_flow.repository.OrderItemRepository;
import com.example.order_flow.service.OrderItemService;
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
public class GetAllOrderItemsUnitTests {
    @Mock
    private OrderItemRepository orderItemRepository;
    @InjectMocks
    private OrderItemService orderItemService;

    @Test
    public void getAllOrderItemsHappyFlow(){
        OrderItem oi1 = new OrderItem();
        oi1.setOrderItemId(4001);
        oi1.setOrderId(3001);
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        oi1.setUnitPrice(BigDecimal.valueOf(30));
        OrderItem oi2 = new OrderItem();
        oi2.setOrderItemId(4002);
        oi2.setOrderId(3001);
        oi2.setProductId(1002);
        oi2.setQuantity(2);
        oi2.setUnitPrice(BigDecimal.valueOf(20));

        given(orderItemRepository.findAll())
                .willReturn(List.of(oi1, oi2));

        List<OrderItemResponseDto> result = orderItemService.getAllOrderItems();

        assertEquals(2, result.size());
        assertEquals(3001, result.get(0).getOrderId());
        assertEquals(3001, result.get(1).getOrderId());

        assertEquals(1001, result.get(0).getProductId());
        assertEquals(1002, result.get(1).getProductId());

        assertEquals(3, result.get(0).getQuantity());
        assertEquals(2, result.get(1).getQuantity());

        verify(orderItemRepository).findAll();
    }

    //empty list
    @Test
    public void emptyOrderItemsListTest(){
        given(orderItemRepository.findAll())
                .willReturn(List.of());

        List<OrderItemResponseDto> result = orderItemService.getAllOrderItems();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
