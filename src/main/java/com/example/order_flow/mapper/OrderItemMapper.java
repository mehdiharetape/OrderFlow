package com.example.order_flow.mapper;

import com.example.order_flow.dto.order.OrderItemCreateDto;
import com.example.order_flow.dto.order.OrderItemRequestDto;
import com.example.order_flow.dto.order.OrderItemResponseDto;
import com.example.order_flow.entity.OrderItem;

public class OrderItemMapper {
    public static OrderItem createInitialOrderItem(OrderItemCreateDto createDto)
    {
        OrderItem item = new OrderItem();
        item.setOrderId(createDto.getOrderId());
        item.setProductId(createDto.getProductId());
        item.setQuantity(createDto.getQuantity());
        item.setUnitPrice(createDto.getUnitPrice());

        return item;
    }

    public static OrderItemResponseDto orderITemToResponse(OrderItem orderItem){
        OrderItemResponseDto item = new OrderItemResponseDto();
        item.setOrderItemId(orderItem.getOrderItemId());
        item.setOrderId(orderItem.getOrderId());
        item.setProductId(orderItem.getProductId());
        item.setQuantity(orderItem.getQuantity());
        item.setUnitPrice(orderItem.getUnitPrice());

        return item;
    }
}
