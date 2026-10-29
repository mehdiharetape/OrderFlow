package com.example.order_flow.order.integration;

import com.example.order_flow.dto.order.OrderItemRequestDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.OrderItem;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.exceptions.InactiveProductException;
import com.example.order_flow.exceptions.InvalidReservationAmountException;
import com.example.order_flow.exceptions.InventoryNotFoundForProductException;
import com.example.order_flow.exceptions.ProductNotFoundException;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.OrderItemRepository;
import com.example.order_flow.service.InventoryService;
import com.example.order_flow.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class OrderCreateIntegrationTest {
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderService service;
    @MockitoSpyBean
    private InventoryMovementRepository movementRepository;

    //@Test
    public void createOrderIntegrationHappyFlow(){
        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1003);
        oi1.setQuantity(2);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1004);
        oi2.setQuantity(1);

        Order order = service.createOrder(List.of(oi1, oi2));
        var items = orderItemRepository.findAllByOrderId(order.getOrderId());
        List<OrderItem> orderItems = new ArrayList<>();
        items.forEach(orderItems::add);
        var i1 = inventoryService.getInventoryByProductId(1003);
        var i2 = inventoryService.getInventoryByProductId(1004);

        assertNotNull(order);
        assertEquals(BigDecimal.valueOf(41.37), order.getTotalAmount());
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        //items
        assertEquals(2, orderItems.get(0).getQuantity());
        assertEquals(1003, orderItems.get(0).getProductId());
        assertEquals(order.getOrderId(), orderItems.get(0).getOrderId());
        assertEquals(BigDecimal.valueOf(15.56), orderItems.get(0).getUnitPrice());

        assertEquals(1, orderItems.get(1).getQuantity());
        assertEquals(1004, orderItems.get(1).getProductId());
        assertEquals(order.getOrderId(), orderItems.get(1).getOrderId());
        assertEquals(BigDecimal.valueOf(10.25), orderItems.get(1).getUnitPrice());
        //inventories
        assertEquals(2, i1.getReservedQuantity());
        assertEquals(1, i2.getReservedQuantity());
    }

    //not found or inactive product
    //@Test
    public void notFoundProductIntegrationTest(){
        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1003);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1100);
        oi2.setQuantity(2);

        assertThrows(ProductNotFoundException.class,
                () -> service.createOrder(List.of(oi1, oi2)));
    }

    //@Test
    public void inactiveProductIntegrationTest(){
        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1002);
        oi2.setQuantity(2);

        assertThrows(InactiveProductException.class,
                () -> service.createOrder(List.of(oi1, oi2)));
    }

    //insufficient inventory for reserve a product
    //@Test
    public void insufficientInventoryIntegrationTest(){
        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1003);
        oi2.setQuantity(10);

        assertThrows(InvalidReservationAmountException.class,
                () -> service.createOrder(List.of(oi1, oi2)));

        var i1 = inventoryService.getInventoryByProductId(1001);
        var i2 = inventoryService.getInventoryByProductId(1003);

        //reservation amount are same(rollback)
        assertEquals(2, i1.getReservedQuantity());
        assertEquals(2, i2.getReservedQuantity());
    }

    //inventory save failure
    //@Test
    public void inventorySaveFailureException(){
        doThrow(new RuntimeException("Simulated Exception"))
                .when(movementRepository).save(any(InventoryMovement.class));

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

        verify(movementRepository).save(any(InventoryMovement.class));
    }

    //product is not in inventory
    @Test
    public void inventoryNotFoundForProductTest(){
        //order items
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(3);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1005);
        oi2.setQuantity(2);

        assertThrows(InventoryNotFoundForProductException.class,
                () -> service.createOrder(List.of(oi1, oi2)));
    }
}
