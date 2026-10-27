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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GetAllOrderItemsByProductIdUnitTests {
    @Mock
    private OrderItemRepository orderItemRepository;
    @InjectMocks
    private OrderItemService orderItemService;

    @Test
    public void getAllOrderItemsHappyFlow(){
        Integer productId = 1001;
        OrderItem oi1 = new OrderItem();
        oi1.setOrderItemId(4001);
        oi1.setOrderId(3001);
        oi1.setProductId(productId);
        oi1.setQuantity(3);
        oi1.setUnitPrice(BigDecimal.valueOf(30));
        OrderItem oi2 = new OrderItem();
        oi2.setOrderItemId(4002);
        oi2.setOrderId(3002);
        oi2.setProductId(productId);
        oi2.setQuantity(2);
        oi2.setUnitPrice(BigDecimal.valueOf(20));

        given(orderItemRepository.findAllByProductId(productId))
                .willReturn(List.of(oi1, oi2));

        List<OrderItemResponseDto> result =
                orderItemService.getAllOrderItemsByProductId(productId);

        assertEquals(2, result.size());
        assertEquals(3001, result.get(0).getOrderId());
        assertEquals(3002, result.get(1).getOrderId());

        assertEquals(productId, result.get(0).getProductId());
        assertEquals(productId, result.get(1).getProductId());

        assertEquals(3, result.get(0).getQuantity());
        assertEquals(2, result.get(1).getQuantity());

        verify(orderItemRepository).findAllByProductId(productId);
    }

    //empty list
    @Test
    public void emptyOrderItemsByProductId(){
        given(orderItemRepository.findAllByProductId(1003))
                .willReturn(List.of());

        List<OrderItemResponseDto> result =
                orderItemService.getAllOrderItemsByProductId(1003);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //null or invalid product id
    @Test
    public void nullOrderIdTest(){
        Integer productId = null;

        assertThrows(IllegalOrNullArgumentException.class,
                () -> orderItemService.getAllOrderItemsByProductId(productId));

        verify(orderItemRepository, never()).findAllByProductId(anyInt());
    }

    @Test
    public void invalidOrderIdTest(){
        Integer productId = 999;

        assertThrows(IllegalOrNullArgumentException.class,
                () -> orderItemService.getAllOrderItemsByProductId(productId));

        verify(orderItemRepository, never()).findAllByProductId(anyInt());
    }
}
