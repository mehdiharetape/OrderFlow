package com.example.order_flow.order.unit;

import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.OrderItem;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.OrderNotFoundException;
import com.example.order_flow.repository.OrderItemRepository;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.service.OrderService;
import com.example.order_flow.utils.OrderResultById;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GetOrderDetailsByOrderIdUnitTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @InjectMocks
    private OrderService service;

    //happy flow
    @Test
    public void getOrderDetailsItemHappyFlow(){
        Integer orderID = 3001;
        Order order = new Order(orderID);
        order.setTotalAmount(BigDecimal.valueOf(120));
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        OrderItem oi1 = new OrderItem(4001);
        oi1.setOrderId(orderID);
        oi1.setProductId(1001);
        oi1.setQuantity(2);
        oi1.setUnitPrice(BigDecimal.valueOf(50));

        OrderItem oi2 = new OrderItem(4002);
        oi2.setOrderId(orderID);
        oi2.setProductId(1002);
        oi2.setQuantity(1);
        oi2.setUnitPrice(BigDecimal.valueOf(70));

        given(orderRepository.findById(orderID))
                .willReturn(Optional.of(order));
        given(orderItemRepository.findAllByOrderId(orderID))
                .willReturn(List.of(oi1, oi2));

        OrderResultById result = service.getOrderDetailById(orderID);

        assertNotNull(result);
        assertNotNull(result.order());
        assertNotNull(result.orderItems());
        assertEquals(2, result.orderItems().size());
        assertEquals(orderID, result.order().getOrderId());
        assertEquals(orderID, result.orderItems().get(0).getOrderId());
        assertEquals(orderID, result.orderItems().get(1).getOrderId());
        assertEquals(1001, result.orderItems().get(0).getProductId());
        assertEquals(1002, result.orderItems().get(1).getProductId());

        verify(orderRepository).findById(orderID);
        verify(orderItemRepository).findAllByOrderId(orderID);
    }

    //empty order item list
    @Test
    public void emptyOrderItemListByOrderIdTest(){
        Integer orderID = 3001;
        Order order = new Order(orderID);
        order.setTotalAmount(BigDecimal.valueOf(120));
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        given(orderRepository.findById(orderID))
                .willReturn(Optional.of(order));
        given(orderItemRepository.findAllByOrderId(orderID))
                .willReturn(List.of());

        OrderResultById result = service.getOrderDetailById(orderID);

        assertNotNull(result);
        assertNotNull(result.order());
        assertNotNull(result.orderItems());
        assertTrue(result.orderItems().isEmpty());

        verify(orderRepository).findById(orderID);
        verify(orderItemRepository).findAllByOrderId(orderID);
    }

    //null or invalid order id
    @Test
    public void nullOrderIdTest(){
        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.getOrderDetailById(null));
    }

    @Test
    public void invalidOrderIdTest(){
        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.getOrderDetailById(2999));
    }

    //order not found
    @Test
    public void notFoundOrderTest(){
        given(orderRepository.findById(3100))
                .willReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> service.getOrderDetailById(3100));
    }
}
