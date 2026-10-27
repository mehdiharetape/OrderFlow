package com.example.order_flow.mapper;

import com.example.order_flow.dto.order.OrderBriefResponseDto;
import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderMapper {
    public static Order createInitialOrderToOrder(BigDecimal totalAmount){
        Order order = new Order();

        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setTotalAmount(totalAmount);

        return order;
    }

    public static OrderBriefResponseDto orderToBriefResponse(Order order){
        OrderBriefResponseDto dto = new OrderBriefResponseDto();
        dto.setOrderId(order.getOrderId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());

        return dto;
    }
}
