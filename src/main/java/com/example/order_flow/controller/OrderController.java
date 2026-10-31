package com.example.order_flow.controller;

import com.example.order_flow.dto.order.OrderBriefResponseDto;
import com.example.order_flow.dto.order.OrderItemRequestDto;
import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.service.OrderService;
import com.example.order_flow.utils.OrderResultById;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping("/order")
    public ResponseEntity<String> createOrder(
            @RequestBody List<OrderItemRequestDto> orderDto)
    {
        Order order = orderService.createOrder(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Order created successfully . order id : " +
                        order.getOrderId() + " | total price : " + order.getTotalAmount());
    }

    @GetMapping("/order")
    public ResponseEntity<List<OrderBriefResponseDto>> getAllOrders(
            @RequestParam(required=false) OrderStatus status)
    {
        if (status != null) {
            var result = orderService.getOrderByStatus(status);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(result);
        }
        var result = orderService.getAllOrders();
        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderResultById> getOrderDetailsByOrderId(
            @PathVariable Integer orderId)
    {
        var result = orderService.getOrderDetailById(orderId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(result);
    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<String> cancelOrder(
            @PathVariable Integer orderId)
    {
        Integer oid = orderService.cancelOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Order '" + oid + "' canceled successfully!");
    }
}
