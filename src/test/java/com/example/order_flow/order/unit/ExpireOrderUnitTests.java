package com.example.order_flow.order.unit;

import com.example.order_flow.dto.inventory.ReservationChangeDto;
import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.OrderItem;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.OrderNotFoundException;
import com.example.order_flow.properties.OrderProperties;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.repository.OrderItemRepository;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.InventoryService;
import com.example.order_flow.service.OrderItemService;
import com.example.order_flow.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ExpireOrderUnitTests {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderItemService orderItemService;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private InventoryRepository inventoryRepository;

    private OrderService service;
    private final Clock clock = Clock.fixed(
            Instant.parse("2026-11-03T13:00:00Z"),
            ZoneOffset.UTC
    );

    @BeforeEach
    void SetUp()
    {
        OrderProperties properties = new OrderProperties();
        properties.setPaymentExpirationMinutes(30);
        service = new OrderService(orderRepository,
                productRepository, inventoryService, orderItemService,
                inventoryRepository, orderItemRepository, properties, clock);
    }

    @Test
    public void expireOrderDateHappyFlow(){
        Integer orderId = 3001;
        //order
        Order order = new Order(orderId);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setCreatedAt(LocalDateTime.parse("2026-11-03T12:00"));
        order.setExpireAt(LocalDateTime.parse("2026-11-03T12:30"));
        //order items
        OrderItem oi1 = new OrderItem(4001);
        oi1.setOrderId(orderId);
        oi1.setProductId(1001);
        oi1.setQuantity(2);
        oi1.setUnitPrice(BigDecimal.valueOf(50));

        OrderItem oi2 = new OrderItem(4002);
        oi2.setOrderId(orderId);
        oi2.setProductId(1002);
        oi2.setQuantity(1);
        oi2.setUnitPrice(BigDecimal.valueOf(70));

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(orderItemRepository.findAllByOrderId(orderId)).willReturn(List.of(oi1, oi2));

        //act
        service.expireOrder(orderId);

        //assert
        assertTrue(LocalDateTime.now().isAfter(order.getExpireAt()));

        verify(orderRepository).findById(orderId);
        verify(orderItemRepository).findAllByOrderId(orderId);

        ArgumentCaptor<ReservationChangeDto> captor =
                ArgumentCaptor.forClass(ReservationChangeDto.class);

        verify(inventoryService).cancelReservation(eq(1001), captor.capture());
        assertEquals(2, captor.getValue().getReserveAmount());
        assertEquals(orderId, captor.getValue().getOrderId());

        verify(inventoryService).cancelReservation(eq(1002), captor.capture());
        assertEquals(1, captor.getValue().getReserveAmount());
        assertEquals(orderId, captor.getValue().getOrderId());

        verify(orderRepository).changeOrderStatus(orderId, OrderStatus.EXPIRED);
    }

    @Test
    public void statusIsNotPendingPaymentTest(){
        Integer orderId = 3001;
        //order
        Order order = new Order(orderId);
        order.setStatus(OrderStatus.CANCELED);
        order.setCreatedAt(LocalDateTime.parse("2026-11-03T12:00"));
        order.setExpireAt(LocalDateTime.parse("2026-11-03T12:30"));

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        //act
        service.expireOrder(orderId);

        verify(orderRepository).findById(orderId);
        verify(orderItemRepository, never()).findAllByOrderId(orderId);
        verify(orderRepository, never()).changeOrderStatus(anyInt(),
                any(OrderStatus.class));
    }

    @Test
    public void orderNotExpiredYetAndServiceShouldReturn(){
        Integer orderId = 3001;
        //order
        Order order = new Order(orderId);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setCreatedAt(LocalDateTime.parse("2026-11-03T12:00"));
        order.setExpireAt(LocalDateTime.parse("2026-11-03T14:00"));

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        //act
        service.expireOrder(orderId);

        verify(orderRepository).findById(orderId);
        verify(orderItemRepository, never()).findAllByOrderId(orderId);
        verify(orderRepository, never()).changeOrderStatus(anyInt(),
                any(OrderStatus.class));
    }

    @Test
    public void orderNotFoundTest(){
        Integer orderId = 3100;

        given(orderRepository.findById(orderId)).willReturn(Optional.empty());

        //act
        assertThrows(OrderNotFoundException.class,
                () -> service.expireOrder(orderId));

        verify(orderRepository).findById(orderId);
        verify(orderItemRepository, never()).findAllByOrderId(anyInt());
        verify(orderRepository, never()).changeOrderStatus(anyInt(),
                any(OrderStatus.class));
    }

    @Test
    public void nullOrderIdTest(){
        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.expireOrder(null));

        verify(orderRepository, never()).findById(anyInt());
        verify(orderItemRepository, never()).findAllByOrderId(anyInt());
        verify(orderRepository, never()).changeOrderStatus(anyInt(),
                any(OrderStatus.class));
    }

    @Test
    public void invalidOrderIdTest(){
        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.expireOrder(2999));

        verify(orderRepository, never()).findById(anyInt());
        verify(orderItemRepository, never()).findAllByOrderId(anyInt());
        verify(orderRepository, never()).changeOrderStatus(anyInt(),
                any(OrderStatus.class));
    }
}
