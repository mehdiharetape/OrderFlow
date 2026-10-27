package com.example.order_flow.validation;

import com.example.order_flow.dto.order.OrderItemRequestDto;
import com.example.order_flow.exceptions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderValidation {
    public static void  orderIdValidation(Integer orderId){
        if (orderId == null || orderId < 3000)
            throw new IllegalOrNullArgumentException();
    }

    public static void orderCreateValidation(List<OrderItemRequestDto> orderItems)
    {
        if (orderItems == null || orderItems.isEmpty())
            throw new NullOrderItemListException();

        Set<Integer> productIds = new HashSet<>();

        for (OrderItemRequestDto item : orderItems)
        {
            if(item == null)
                throw new InvalidOrderItemException();

            ProductValidation.productIdValidation(item.getProductId());

            if(item.getQuantity() == null || item.getQuantity() <= 0)
                throw new InvalidOrderItemException();

            if(!productIds.add(item.getProductId())){
                throw new DuplicateProductInOrderException(item.getProductId());
            }
        }
    }
}
