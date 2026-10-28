package com.example.order_flow.mapper;

import com.example.order_flow.dto.movement.CreateMovementDto;
import com.example.order_flow.dto.movement.InventoryMovementResponseDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.entity.enums.MovementReason;

import java.time.LocalDateTime;

public class InventoryMovementMapper {

    public static InventoryMovement createInventoryMovement(CreateMovementDto dto)
    {
        InventoryMovement im = new InventoryMovement();
        im.setOrderId(dto.getOrderId());
        im.setProductId(dto.getProductId());
        im.setPhysicalChange(dto.getPhysicalChange());
        im.setReserveChange(dto.getReservedChange());
        im.setCreatedAt(dto.getCreatedAt());
        im.setReason(dto.getReason());

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
