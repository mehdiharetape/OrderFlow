package com.example.order_flow.utils;

import com.example.order_flow.dto.order.OrderBriefResponseDto;
import com.example.order_flow.dto.order.OrderItemResponseDto;

import java.util.List;

public record OrderResultById(OrderBriefResponseDto order,
                              List<OrderItemResponseDto> orderItems) {}
