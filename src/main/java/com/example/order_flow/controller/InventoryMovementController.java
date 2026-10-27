package com.example.order_flow.controller;

import com.example.order_flow.dto.movement.InventoryMovementResponseDto;
import com.example.order_flow.service.InventoryMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class InventoryMovementController {
    private final InventoryMovementService movementService;

    @Autowired
    public InventoryMovementController(InventoryMovementService movementService){
        this.movementService = movementService;
    }

    @GetMapping("/movement")
    public ResponseEntity<List<InventoryMovementResponseDto>> getAllInventories(){
        var result = movementService.getAllMovements();
        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/movement/{productId}")
    public ResponseEntity<List<InventoryMovementResponseDto>>
            getInventoryByProductId(@PathVariable Integer productId)
    {
        var result = movementService.getMovementListByProductId(productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }
}
