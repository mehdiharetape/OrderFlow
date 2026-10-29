package com.example.order_flow.controller;

import com.example.order_flow.dto.inventory.ChangeTotalQuantityDto;
import com.example.order_flow.dto.inventory.CreateInventoryDto;
import com.example.order_flow.dto.inventory.InventoryDetailResponseDto;
import com.example.order_flow.dto.inventory.ReservationChangeDto;
import com.example.order_flow.service.InventoryService;
import com.example.order_flow.utils.InventoryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InventoryController {
    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService){
        this.inventoryService = inventoryService;
    }

    @PostMapping("/inventory")
    public ResponseEntity<String> createInventory(
            @RequestBody CreateInventoryDto createDto)
    {
        InventoryResult result = inventoryService.createInventory(createDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body("inventory with id '" + result.inventory() +"' and " +
                        "inventory movement with id '" + result.inventoryMovement() +
                        "' created!");
    }

    @GetMapping("/inventory/{inventoryId}")
    public ResponseEntity<InventoryDetailResponseDto> getInventoryById(
            @PathVariable Integer inventoryId)
    {
        var result = inventoryService.getInventoryById(inventoryId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(result);
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryDetailResponseDto>> getAllInventories(){
        var result = inventoryService.getAllInventories();
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(result);
    }

    @GetMapping("/inventory/{productId}/product")
    public ResponseEntity<InventoryDetailResponseDto> getInventoryByProductId(
            @PathVariable Integer productId)
    {
        var result = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(result);
    }

    @PatchMapping("/inventory/{productId}/quantity")
    public ResponseEntity<String> changeTotalQuantity(
            @PathVariable Integer productId,
            @RequestBody ChangeTotalQuantityDto dto)
    {
        var result = inventoryService.changeTotalQuantity(productId, dto);
        return ResponseEntity.status(HttpStatus.OK).
                body("Inventory with product id '" + productId + "' changed by amount '"
                + dto.getAmount() + "' and movement '" +
                        result.inventoryMovement() + "' created.");
    }
}
