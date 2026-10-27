package com.example.order_flow.orderitem.unit;

import com.example.order_flow.dto.order.OrderItemResponseDto;
import com.example.order_flow.entity.OrderItem;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GetOrderItemsByOrderIdUnitTest {
    @Mock
    private OrderItemRepository orderItemRepository;
    @InjectMocks
    private OrderItemService orderItemService;

    @Test
    public void getOrderItemsByOrderIdHappyFlow(){
        Integer orderId = 3001;
        OrderItem oi1 = new OrderItem();
        oi1.setOrderItemId(4001);
        oi1.setOrderId(orderId);
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        oi1.setUnitPrice(BigDecimal.valueOf(30));
        OrderItem oi2 = new OrderItem();
        oi2.setOrderItemId(4002);
        oi2.setOrderId(orderId);
        oi2.setProductId(1002);
        oi2.setQuantity(2);
        oi2.setUnitPrice(BigDecimal.valueOf(20));

        given(orderItemRepository.findAllByOrderId(orderId))
                .willReturn(List.of(oi1, oi2));

        List<OrderItemResponseDto> result =
                orderItemService.getAllOrderItemsByOrderId(orderId);

        assertEquals(2, result.size());
        assertEquals(orderId, result.get(0).getOrderId());
        assertEquals(orderId, result.get(1).getOrderId());

        assertEquals(1001, result.get(0).getProductId());
        assertEquals(1002, result.get(1).getProductId());

        assertEquals(3, result.get(0).getQuantity());
        assertEquals(2, result.get(1).getQuantity());

        verify(orderItemRepository).findAllByOrderId(orderId);
    }

    //empty list
    @Test
    public void emptyOrderItemsListByOrderId(){
        given(orderItemRepository.findAllByOrderId(3001))
                .willReturn(List.of());

        List<OrderItemResponseDto> result =
                orderItemService.getAllOrderItemsByOrderId(3001);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //null or invalid order item
    @Test
    public void nullOrderIdTest(){
        Integer orderId = null;

        assertThrows(IllegalOrNullArgumentException.class,
                () -> orderItemService.getAllOrderItemsByOrderId(orderId));

        verify(orderItemRepository, never()).findAllByOrderId(anyInt());
    }

    @Test
    public void invalidOrderIdTest(){
        Integer orderId = 2999;

        assertThrows(IllegalOrNullArgumentException.class,
                () -> orderItemService.getAllOrderItemsByOrderId(orderId));

        verify(orderItemRepository, never()).findAllByOrderId(anyInt());
    }
}
