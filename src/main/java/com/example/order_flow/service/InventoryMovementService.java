package com.example.order_flow.service;

import com.example.order_flow.dto.movement.CreateMovementDto;
import com.example.order_flow.dto.movement.InventoryMovementResponseDto;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.exceptions.OrderNotFoundException;
import com.example.order_flow.mapper.InventoryMovementMapper;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.validation.InventoryMovementValidation;
import com.example.order_flow.validation.ProductValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryMovementService {
    private InventoryMovementRepository movementRepository;
    private OrderRepository orderRepository;

    @Autowired
    public InventoryMovementService(InventoryMovementRepository movementRepository,
                                    OrderRepository orderRepository)
    {
        this.movementRepository = movementRepository;
        this.orderRepository = orderRepository;
    }

    //create inventory movement
    public Integer createInventoryMovement(CreateMovementDto dto)
    {
        Integer orderId = dto.getOrderId();
        ProductValidation.productIdValidation(dto.getProductId());

        InventoryMovementValidation.createInventoryMovementValidation(dto);

        if(orderId != null && !orderRepository.existsById(orderId))
            throw new OrderNotFoundException(orderId);

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
