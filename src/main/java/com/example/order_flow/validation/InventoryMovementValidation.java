package com.example.order_flow.validation;

import com.example.order_flow.dto.movement.CreateMovementDto;
import com.example.order_flow.entity.enums.MovementReason;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;

public class InventoryMovementValidation {

    public static void createInventoryMovementValidation(CreateMovementDto dto){
        Integer orderId = dto.getOrderId();
        if (dto.getReason() == null)
            throw new IllegalOrNullArgumentException();

        if (dto.getCreatedAt() == null)
            throw new IllegalOrNullArgumentException();

        if(orderId == null && dto.getReason() != MovementReason.INITIAL_STOCK)
            throw new IllegalOrNullArgumentException();
    }
}
