package com.example.order_flow.mapper;

import com.example.order_flow.dto.movement.InventoryMovementResponseDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.entity.enums.MovementReason;

import java.time.LocalDateTime;

public class InventoryMovementMapper {

    public static InventoryMovement toInventoryMovementInitial(
            Integer productId, Integer physicalChange)
    {
        InventoryMovement im = new InventoryMovement();
        im.setOrderId(null);
        im.setProductId(productId);
        im.setPhysicalChange(physicalChange);
        im.setReserveChange(0);
        im.setCreatedAt(LocalDateTime.now());
        im.setReason(MovementReason.INITIAL_STOCK);

        return im;
    }

    public static InventoryMovement toInventoryMovementQuantityChange(
            Integer productId, Integer changeAmount)
    {
        InventoryMovement im = new InventoryMovement();
        im.setOrderId(null);
        im.setProductId(productId);
        im.setPhysicalChange(changeAmount);
        im.setReserveChange(0);
        im.setCreatedAt(LocalDateTime.now());
        im.setReason(MovementReason.CHANGE_TOTAL_QUANTITY);

        return im;
    }

    public static InventoryMovement toInventoryMovementReservedChange(
            Integer productId, Integer changeAmount)
    {
        InventoryMovement im = new InventoryMovement();
        im.setOrderId(null);
        im.setProductId(productId);
        im.setPhysicalChange(0);
        im.setReserveChange(changeAmount);
        im.setCreatedAt(LocalDateTime.now());
        if(changeAmount > 0)
            im.setReason(MovementReason.RESERVATION);
        if (changeAmount < 0)
            im.setReason(MovementReason.CANCEL_RESERVATION);

        return im;
    }

    public static InventoryMovementResponseDto inventoryToResponse(
            InventoryMovement movement)
    {
        InventoryMovementResponseDto dto = new InventoryMovementResponseDto();
        dto.setInventoryMovementId(movement.getInventoryMovementId());
        dto.setOrderId(movement.getOrderId());
        dto.setProductId(movement.getProductId());
        dto.setPhysicalChange(movement.getPhysicalChange());
        dto.setReservedChange(movement.getReservedChange());
        dto.setCreatedAt(movement.getCreatedAt());
        dto.setReason(movement.getReason());

        return dto;
    }
}
