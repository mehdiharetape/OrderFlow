package com.example.order_flow.order.unit;

import com.example.order_flow.dto.inventory.ReservationChangeDto;
import com.example.order_flow.dto.order.OrderItemRequestDto;
import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.Product;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.entity.enums.ProductStatus;
import com.example.order_flow.exceptions.*;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.InventoryService;
import com.example.order_flow.service.OrderItemService;
import com.example.order_flow.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderCreationUnitTests {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderItemService orderItemService;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private InventoryRepository inventoryRepository;
    @InjectMocks
    private OrderService service;

    //happy flow
    @Test
    public void createNewOrderHappyFlow(){
        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1002);
        oi2.setQuantity(2);
        //products
        Product p1 = new Product(1001);
        p1.setCurrentPrice(BigDecimal.valueOf(20));
        p1.setStatus(ProductStatus.ACTIVE);
        Product p2 = new Product(1002);
        p2.setCurrentPrice(BigDecimal.valueOf(30));
        p2.setStatus(ProductStatus.ACTIVE);
        //assertions
        given(productRepository.findById(1001)).willReturn(Optional.of(p1));
        given(productRepository.findById(1002)).willReturn(Optional.of(p2));
        given(inventoryRepository.existsByProductId(1001)).willReturn(true);
        given(inventoryRepository.existsByProductId(1002)).willReturn(true);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                    Order save = invocation.getArgument(0);
                    save.setOrderId(3001);
                    return save;});
        //act
        Order order = service.createOrder(List.of(oi1, oi2));

        //assertion
        assertEquals(3001, order.getOrderId());
        assertEquals(BigDecimal.valueOf(120), order.getTotalAmount());
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());

        verify(orderRepository).save(any(Order.class));
        verify(orderItemService).createOrderItems(eq(3001),
                eq(List.of(oi1, oi2)));

        ArgumentCaptor<ReservationChangeDto> captor =
                ArgumentCaptor.forClass(ReservationChangeDto.class);

        verify(inventoryService).reservation(eq(1001), captor.capture());
        assertEquals(3, captor.getValue().getReserveAmount());
        assertEquals(3001, captor.getValue().getOrderId());
        verify(inventoryService).reservation(eq(1002), captor.capture());
        assertEquals(2, captor.getValue().getReserveAmount());
        assertEquals(3001, captor.getValue().getOrderId());
    }

    //null or empty order items
    @Test
    public void nullOrderItemsListTest(){
        List<OrderItemRequestDto> items = null;
        assertThrows(NullOrderItemListException.class,
                () -> service.createOrder(items));
    }

    @Test
    public void emptyOrderItemsListTest(){
        List<OrderItemRequestDto> items = new ArrayList<>();
        assertThrows(NullOrderItemListException.class,
                () -> service.createOrder(items));
    }

    //null item
    @Test
    public void nullSingleItemInOrderItemsLis(){
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);

        List<OrderItemRequestDto> items = Arrays.asList(oi1, null);

        assertThrows(InvalidOrderItemException.class,
                () -> service.createOrder(items));
    }

    //zero or negative or null item quantity
    @Test
    public void zeroOrderItemQuantityTest(){
        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1002);
        oi2.setQuantity(0);

        assertThrows(InvalidOrderItemException.class,
                () -> service.createOrder(List.of(oi1, oi2)));
    }

    @Test
    public void negativeOrderItemQuantityTest(){
        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1002);
        oi2.setQuantity(-1);

        assertThrows(InvalidOrderItemException.class,
                () -> service.createOrder(List.of(oi1, oi2)));
    }

    @Test
    public void nullOrderItemQuantityTest(){
        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1002);
        oi2.setQuantity(null);

        assertThrows(InvalidOrderItemException.class,
                () -> service.createOrder(List.of(oi1, oi2)));
    }

    //null or invalid product id
    @Test
    public void nullProductIdTest(){
        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(null);
        oi2.setQuantity(2);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.createOrder(List.of(oi1, oi2)));
    }

    @Test
    public void invalidProductIdTest(){
        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(999);
        oi2.setQuantity(2);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> service.createOrder(List.of(oi1, oi2)));
    }

    //product not found
    @Test
    public void productNotFoundTest(){
        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1100);
        oi2.setQuantity(2);
        //products
        Product p1 = new Product(1001);
        p1.setCurrentPrice(BigDecimal.valueOf(20));
        p1.setStatus(ProductStatus.ACTIVE);
        Product p2 = new Product(1100);
        p2.setCurrentPrice(BigDecimal.valueOf(20));
        p2.setStatus(ProductStatus.ACTIVE);

        given(productRepository.findById(1001)).willReturn(Optional.of(p1));
        given(inventoryRepository.existsByProductId(1001)).willReturn(true);

        assertThrows(ProductNotFoundException.class,
                () -> service.createOrder(List.of(oi1, oi2)));

        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemService, never()).createOrderItems(anyInt(), anyList());
    }

    //duplicated product id
    @Test
    public void duplicateProductIdInOrderItemsList(){
        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1001);
        oi2.setQuantity(2);

        assertThrows(DuplicateProductInOrderException.class,
                () -> service.createOrder(List.of(oi1, oi2)));
    }

    //inactive product
    @Test
    public void inactiveProductTest(){
        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        //products
        Product p1 = new Product(1001);
        p1.setCurrentPrice(BigDecimal.valueOf(20));
        p1.setStatus(ProductStatus.INACTIVE);

        given(productRepository.findById(1001)).willReturn(Optional.of(p1));
        given(inventoryRepository.existsByProductId(1001)).willReturn(true);

        assertThrows(InactiveProductException.class,
                () -> service.createOrder(List.of(oi1)));

        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemService, never()).createOrderItems(anyInt(), anyList());
    }

    //inventory not found for this product
    @Test
    public void inventoryNotFoundForProductTest(){
        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        //products
        Product p1 = new Product(1001);
        p1.setCurrentPrice(BigDecimal.valueOf(20));
        p1.setStatus(ProductStatus.ACTIVE);

        given(productRepository.findById(1001)).willReturn(Optional.of(p1));
        given(inventoryRepository.existsByProductId(1001)).willReturn(false);

        assertThrows(InventoryNotFoundForProductException.class,
                () -> service.createOrder(List.of(oi1)));

        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemService, never()).createOrderItems(anyInt(), anyList());
    }
}
