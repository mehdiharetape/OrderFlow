package com.example.order_flow.validation;

import com.example.order_flow.dto.order.OrderItemRequestDto;
import com.example.order_flow.exceptions.InvalidOrderItemException;

import java.util.List;

public class OrderItemValidation {
    public static void createOrderItemValidation(Integer orderId ,
                                                 List<OrderItemRequestDto> requestsDto)
    {
        OrderValidation.orderIdValidation(orderId);

        for (OrderItemRequestDto request : requestsDto){
            if(request == null)
                throw new InvalidOrderItemException();

            ProductValidation.productIdValidation(request.getProductId());

            if(request.getQuantity() == null || request.getQuantity() <= 0)
                throw new InvalidOrderItemException();
        }
    }
}
