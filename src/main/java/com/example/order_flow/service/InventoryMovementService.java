package com.example.order_flow.service;

import com.example.order_flow.dto.movement.CreateMovementDto;
import com.example.order_flow.dto.movement.InventoryMovementResponseDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.mapper.InventoryMovementMapper;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.validation.ProductValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryMovementService {
    private InventoryMovementRepository movementRepository;

    @Autowired
    public InventoryMovementService(InventoryMovementRepository movementRepository){
        this.movementRepository = movementRepository;
    }

    //create inventory movement
    public Integer createInventoryMovement(CreateMovementDto dto)
    {
        ProductValidation.productIdValidation(dto.getProductId());

         return movementRepository.save(InventoryMovementMapper
                .createInventoryMovement(dto)).getInventoryMovementId();
    }

    //get all movement
    public List<InventoryMovementResponseDto> getAllMovements(){
        Iterable<InventoryMovement> movements = movementRepository.findAll();

        List<InventoryMovementResponseDto> result = new ArrayList<>();

        movements.forEach(m -> result.add(InventoryMovementMapper
                .inventoryToResponse(m)));

        return result;
    }

    //get movements for a product id
    public List<InventoryMovementResponseDto> getMovementListByProductId(Integer productId)
    {
        ProductValidation.productIdValidation(productId);

        Iterable<InventoryMovement> movements =
                movementRepository.findMovementsByProductId(productId);

        List<InventoryMovementResponseDto> result = new ArrayList<>();
        movements.forEach(m -> result.add(
                InventoryMovementMapper.inventoryToResponse(m)));

        return result;
    }
}
