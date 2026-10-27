package com.example.order_flow.orderitem.unit;

import com.example.order_flow.dto.order.OrderItemRequestDto;
import com.example.order_flow.dto.order.OrderItemResponseDto;
import com.example.order_flow.entity.OrderItem;
import com.example.order_flow.entity.Product;
import com.example.order_flow.entity.enums.ProductStatus;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.InvalidOrderItemException;
import com.example.order_flow.exceptions.OrderNotFoundException;
import com.example.order_flow.exceptions.ProductNotFoundException;
import com.example.order_flow.repository.OrderItemRepository;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.OrderItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateOrderItemUnitTests {
    @Mock
    private OrderItemRepository repository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private OrderItemService service;

    //happy flow
    @Test
    public void createOrderItemHappyFlow(){
        Integer orderId = 3019;
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(2);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1002);
        oi2.setQuantity(3);

        Product p1 = new Product(1001);
        p1.setCurrentPrice(BigDecimal.valueOf(20));
        Product p2 = new Product(1002);
        p2.setCurrentPrice(BigDecimal.valueOf(30));

        when(repository.save(any(OrderItem.class)))
                .thenAnswer(invocation -> {
                    OrderItem saved = invocation.getArgument(0);
                    saved.setOrderItemId(5023);
                    return saved;
                }).thenAnswer(invocation -> {
                    OrderItem saved = invocation.getArgument(0);
                    saved.setOrderItemId(5024);
                    return saved;
                });

        given(orderRepository.existsById(orderId)).willReturn(true);
        given(productRepository.findById(1001)).willReturn(Optional.of(p1));
        given(productRepository.findById(1002)).willReturn(Optional.of(p2));

        List<Integer> result = service.createOrderItems(orderId, List.of(oi1, oi2));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(orderRepository).existsById(orderId);
        verify(productRepository).findById(1001);
        verify(productRepository).findById(1002);
        verify(repository, times(2)).save(any(OrderItem.class));

        ArgumentCaptor<OrderItem> captor = ArgumentCaptor
                .forClass(OrderItem.class);
        verify(repository, times(2)).save(captor.capture());
        List<OrderItem> savedItems = captor.getAllValues();

        OrderItem first = savedItems.get(0);
        assertEquals(orderId, first.getOrderId());
        assertEquals(Integer.valueOf(1001), first.getProductId());
        assertEquals(Integer.valueOf(2), first.getQuantity());

        OrderItem second = savedItems.get(1);
        assertEquals(orderId, second.getOrderId());
        assertEquals(Integer.valueOf(1002), second.getProductId());
        assertEquals(Integer.valueOf(3), second.getQuantity());
    }

    //null or invalid order id
    @Test
    public void nullOrderIdItemForItemListTest(){
        Integer orderId = null;
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(2);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1002);
        oi2.setQuantity(3);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.createOrderItems(orderId, List.of(oi1, oi2)));
    }

    @Test
    public void invalidOrderIdItemForItemListTest(){
        Integer orderId = 2999;
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(2);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1002);
        oi2.setQuantity(3);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.createOrderItems(orderId, List.of(oi1, oi2)));
    }

    //order not found
    @Test
    public void orderNotFoundExceptionTest(){
        Integer orderId = 3999;
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(2);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1002);
        oi2.setQuantity(3);

        given(orderRepository.existsById(orderId)).willReturn(false);

        assertThrows(OrderNotFoundException.class,
                () -> service.createOrderItems(orderId, List.of(oi1, oi2)));

        verify(orderRepository).existsById(orderId);
        verify(productRepository, never()).findById(anyInt());
        verify(repository, never()).save(any(OrderItem.class));
    }

    //null request
    @Test
    public void nullSingleOrderRequestTest(){
        Integer orderId = 3019;
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(2);

        List<OrderItemRequestDto> items = Arrays.asList(oi1, null);

        assertThrows(InvalidOrderItemException.class,
                () -> service.createOrderItems(orderId, items));
    }

    //null or invalid product id
    @Test
    public void nullProductIdTest(){
        Integer orderId = 3019;
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(null);
        oi2.setQuantity(2);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.createOrderItems(orderId,List.of(oi1, oi2)));
    }

    @Test
    public void invalidProductIdTest(){
        Integer orderId = 3019;
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(999);
        oi2.setQuantity(2);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.createOrderItems(orderId,List.of(oi1, oi2)));
    }

    //product not found
    @Test
    public void productNotFoundExceptionTest(){
        Integer orderId = 3019;
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1100);
        oi2.setQuantity(2);

        Product p1 = new Product(1001);
        p1.setCurrentPrice(BigDecimal.valueOf(20));
        Product p2 = new Product(1100);
        p2.setCurrentPrice(BigDecimal.valueOf(30));

        given(orderRepository.existsById(orderId)).willReturn(true);
        given(productRepository.findById(1001)).willReturn(Optional.of(p1));
        given(productRepository.findById(1100)).willReturn(Optional.empty());
        when(repository.save(any(OrderItem.class)))
                .thenAnswer(invocation -> {
                    OrderItem saved = invocation.getArgument(0);
                    saved.setOrderItemId(5019);
                    return saved;
                });

        assertThrows(ProductNotFoundException.class,
                () -> service.createOrderItems(orderId,List.of(oi1, oi2)));

        verify(orderRepository).existsById(orderId);
        verify(productRepository).findById(1001);
        verify(repository).save(any(OrderItem.class));
    }

    //negative or zero or null quantity
    @Test
    public void zeroItemQuantityTest(){
        Integer orderId = 3019;
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(0);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1002);
        oi2.setQuantity(2);

        assertThrows(InvalidOrderItemException.class,
                () -> service.createOrderItems(orderId, List.of(oi1, oi2)));
    }

    @Test
    public void negativeItemQuantityTest(){
        Integer orderId = 3019;
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(-1);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1002);
        oi2.setQuantity(2);

        assertThrows(InvalidOrderItemException.class,
                () -> service.createOrderItems(orderId, List.of(oi1, oi2)));
    }

    @Test
    public void nullItemQuantityTest(){
        Integer orderId = 3019;
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1002);
        oi2.setQuantity(null);

        assertThrows(InvalidOrderItemException.class,
                () -> service.createOrderItems(orderId, List.of(oi1, oi2)));
    }
}
