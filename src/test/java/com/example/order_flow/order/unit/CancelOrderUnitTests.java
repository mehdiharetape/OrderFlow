package com.example.order_flow.order.unit;

import com.example.order_flow.dto.inventory.ReservationChangeDto;
import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.OrderItem;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.OrderCancelFailException;
import com.example.order_flow.exceptions.OrderNotFoundException;
import com.example.order_flow.repository.OrderItemRepository;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.service.InventoryService;
import com.example.order_flow.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CancelOrderUnitTests {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository itemRepository;
    @Mock
    private InventoryService inventoryService;
    @InjectMocks
    private OrderService orderService;

    @Test
    public void orderCancelHappyFlow()
    {
        //arrange
        Integer orderId = 3001;
        Order order = new Order(orderId);
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        OrderItem oi1 = new OrderItem(4001);
        oi1.setOrderId(orderId);
        oi1.setProductId(1001);
        oi1.setQuantity(2);
        OrderItem oi2 = new OrderItem(4002);
        oi2.setOrderId(orderId);
        oi2.setProductId(1002);
        oi2.setQuantity(3);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(itemRepository.findAllByOrderId(orderId)).willReturn(List.of(oi1, oi2));

        Integer result = orderService.cancelOrder(orderId);

        assertEquals(orderId, result);
        ArgumentCaptor<ReservationChangeDto> captor =
                ArgumentCaptor.forClass(ReservationChangeDto.class);

        verify(inventoryService, times(2))
                .cancelReservation(anyInt(), captor.capture());
        List<ReservationChangeDto> changes = captor.getAllValues();
        assertEquals(orderId, changes.get(0).getOrderId());
        assertEquals(2, changes.get(0).getReserveAmount());

        assertEquals(orderId, changes.get(1).getOrderId());
        assertEquals(3, changes.get(1).getReserveAmount());

        verify(inventoryService).cancelReservation(eq(1001),
                any(ReservationChangeDto.class));
        verify(inventoryService).cancelReservation(eq(1002),
                any(ReservationChangeDto.class));
        verify(orderRepository).changeOrderStatus(orderId, OrderStatus.CANCELED);
    }

    //wrong order status
    @Test
    public void wrongOrderStatusTest(){
        Integer orderId = 3001;
        Order order = new Order(orderId);
        order.setStatus(OrderStatus.PAID);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        assertThrows(OrderCancelFailException.class,
                () -> orderService.cancelOrder(orderId));

        verify(orderRepository).findById(orderId);
        verify(itemRepository, never()).findAllByOrderId(orderId);
        verify(inventoryService, never()).cancelReservation(anyInt(),
                any(ReservationChangeDto.class));
        verify(orderRepository, never())
                .changeOrderStatus(anyInt(),any(OrderStatus.class));
    }

    //empty order item
    @Test
    public void emptyOrderItemListTest(){
        Integer orderId = 3001;
        Order order = new Order(orderId);
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(itemRepository.findAllByOrderId(orderId)).willReturn(List.of());

        assertThrows(OrderCancelFailException.class,
                () -> orderService.cancelOrder(orderId));

        verify(orderRepository).findById(orderId);
        verify(itemRepository).findAllByOrderId(orderId);
        verify(inventoryService, never()).cancelReservation(anyInt(),
                any(ReservationChangeDto.class));
        verify(orderRepository, never())
                .changeOrderStatus(anyInt(),any(OrderStatus.class));
    }

    //order not found
    @Test
    public void orderNotFoundTest(){
        Integer orderId = 3600;
        given(orderRepository.findById(orderId)).willReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> orderService.cancelOrder(orderId));

        verify(orderRepository).findById(orderId);
        verify(itemRepository, never()).findAllByOrderId(orderId);
        verify(inventoryService, never()).cancelReservation(anyInt(),
                any(ReservationChangeDto.class));
        verify(orderRepository, never())
                .changeOrderStatus(anyInt(),any(OrderStatus.class));
    }

    //null or invalid order id
    @Test
    public void nullOrderIdTest()
    {
        assertThrows(IllegalOrNullArgumentException.class,
                () -> orderService.cancelOrder(null));

        verify(orderRepository, never()).findById(anyInt());
        verify(itemRepository, never()).findAllByOrderId(anyInt());
        verify(inventoryService, never()).cancelReservation(anyInt(),
                any(ReservationChangeDto.class));
        verify(orderRepository, never())
                .changeOrderStatus(anyInt(),any(OrderStatus.class));
    }

    @Test
    public void invalidOrderIdTest()
    {
        assertThrows(IllegalOrNullArgumentException.class,
                () -> orderService.cancelOrder(2999));

        verify(orderRepository, never()).findById(anyInt());
        verify(itemRepository, never()).findAllByOrderId(anyInt());
        verify(inventoryService, never()).cancelReservation(anyInt(),
                any(ReservationChangeDto.class));
        verify(orderRepository, never())
                .changeOrderStatus(anyInt(),any(OrderStatus.class));
    }
}


















