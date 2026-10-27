package com.example.order_flow.orderitem.integration;

import com.example.order_flow.dto.order.OrderItemRequestDto;
import com.example.order_flow.entity.OrderItem;
import com.example.order_flow.exceptions.OrderNotFoundException;
import com.example.order_flow.exceptions.ProductNotFoundException;
import com.example.order_flow.repository.OrderItemRepository;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.service.OrderItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CreateOrderItemIntegrationTests {
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderItemService service;

    //happy flow
    //@Test
    public void createOrderItemIntegrationTest(){
        Integer orderId = 3019;
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1001);
        oi1.setQuantity(1);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1002);
        oi2.setQuantity(1);

        List<Integer> result = service.createOrderItems(orderId, List.of(oi1, oi2));


        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());

        OrderItem item1 = orderItemRepository.findById(result.get(0)).orElseThrow();
        OrderItem item2 = orderItemRepository.findById(result.get(1)).orElseThrow();

        assertEquals(orderId, item1.getOrderId());
        assertEquals(orderId, item2.getOrderId());

        assertEquals(1001, item1.getProductId());
        assertEquals(1, item1.getQuantity());

        assertEquals(1002, item2.getProductId());
        assertEquals(1, item1.getQuantity());

        assertEquals(0,
                BigDecimal.valueOf(59.32).compareTo(item1.getUnitPrice()));
        assertEquals(0,
                BigDecimal.valueOf(56.25).compareTo(item2.getUnitPrice()));
    }

    //order not found
    @Test
    public void orderNotFoundExceptionTest(){
        Integer orderId = 3100;
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1002);
        oi1.setQuantity(1);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1001);
        oi2.setQuantity(1);

        assertThrows(OrderNotFoundException.class,
                () -> service.createOrderItems(orderId, List.of(oi1, oi2)));
    }

    //product not found (Transactional)
    @Test
    public void productNotFoundExceptionTest(){
        Integer orderId = 3019;
        OrderItemRequestDto oi1 = new OrderItemRequestDto();
        oi1.setProductId(1002);
        oi1.setQuantity(1);
        OrderItemRequestDto oi2 = new OrderItemRequestDto();
        oi2.setProductId(1100);
        oi2.setQuantity(1);

        assertThrows(ProductNotFoundException.class,
                () -> service.createOrderItems(orderId, List.of(oi1, oi2)));
    }
}
