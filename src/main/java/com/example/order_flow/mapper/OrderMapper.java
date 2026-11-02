package com.example.order_flow.mapper;

import com.example.order_flow.dto.order.OrderBriefResponseDto;
import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

public class OrderMapper
{
    private static Clock clock;

    public OrderMapper(Clock clock){
        this.clock = clock;
    }

    @Value("${order.payment-expiration-minutes}")
    private static Integer expireMinute;

    public static Order createInitialOrderToOrder(BigDecimal totalAmount,
                                                  LocalDateTime createdAt, LocalDateTime expireAt)
    {
        Order order = new Order();

        order.setCreatedAt(createdAt);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setTotalAmount(totalAmount);
        order.setExpireAt(expireAt);

        return order;
    }

    public static OrderBriefResponseDto orderToBriefResponse(Order order){
        OrderBriefResponseDto dto = new OrderBriefResponseDto();
        dto.setOrderId(order.getOrderId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setExpireAt(order.getExpireAt());

        return dto;
    }
}
