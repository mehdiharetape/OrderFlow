package com.example.order_flow.mapper;

import com.example.order_flow.dto.inventory.CreateInventoryDto;
import com.example.order_flow.dto.inventory.InventoryDetailResponseDto;
import com.example.order_flow.entity.Inventory;

public class InventoryMapper {

    public static Inventory createDtoToInventory(CreateInventoryDto dto){
        Inventory inventory = new Inventory();

        inventory.setProductId(dto.getProductId());
        inventory.setTotalQuantity(dto.getTotalQuantity());
        inventory.setReservedQuantity(0);

        return inventory;
    }

    public static InventoryDetailResponseDto inventoryToResponse(Inventory inventory){
        int totalQuantity = inventory.getTotalQuantity();
        int reservedQuantity = inventory.getReservedQuantity();
        int available = totalQuantity - reservedQuantity;

        var response = new InventoryDetailResponseDto();
        response.setInventoryId(inventory.getInventoryId());
        response.setProductId(inventory.getProductId());
        response.setTotalQuantity(totalQuantity);
        response.setReservedQuantity(reservedQuantity);
        response.setAvailable(available);

        return response;
    }
}
