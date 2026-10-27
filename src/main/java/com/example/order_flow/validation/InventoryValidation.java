package com.example.order_flow.validation;

import com.example.order_flow.dto.inventory.CreateInventoryDto;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;

public class InventoryValidation {

    public static void createInventoryValidation(CreateInventoryDto dto){
        if(dto.getProductId() < 0 || dto.getProductId() < 1000)
            throw new IllegalOrNullArgumentException();

        if(dto.getTotalQuantity() < 0)
            throw new IllegalOrNullArgumentException();
    }

    public static void inventoryIdValidation(Integer inventoryId){
        if(inventoryId == null || inventoryId < 2000)
            throw new IllegalOrNullArgumentException();
    }
}
