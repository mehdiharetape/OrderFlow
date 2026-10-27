package com.example.order_flow.service;

import com.example.order_flow.dto.inventory.ChangeTotalQuantityDto;
import com.example.order_flow.dto.inventory.CreateInventoryDto;
import com.example.order_flow.dto.inventory.InventoryDetailResponseDto;
import com.example.order_flow.dto.inventory.ReservationChangeDto;
import com.example.order_flow.entity.Inventory;
import com.example.order_flow.entity.InventoryMovement;
import com.example.order_flow.exceptions.*;
import com.example.order_flow.mapper.InventoryMapper;
import com.example.order_flow.mapper.InventoryMovementMapper;
import com.example.order_flow.repository.InventoryMovementRepository;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.utils.InventoryResult;
import com.example.order_flow.validation.InventoryValidation;
import com.example.order_flow.validation.ProductValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryMovementRepository movementRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository,
                            ProductRepository productRepository,
                            InventoryMovementRepository movementRepository)
    {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
    }

    //create inventory
    @Transactional
    public InventoryResult createInventory(CreateInventoryDto inventoryDto)
    {
        InventoryValidation.createInventoryValidation(inventoryDto);

        int productId = inventoryDto.getProductId();
        int totalQuantity = inventoryDto.getTotalQuantity();

        if(!productRepository.existsById(productId))
            throw new ProductNotFoundException();

        if(inventoryRepository.existsByProductId(productId))
            throw new InventoryProductExistException();

        Inventory inventory = InventoryMapper.createDtoToInventory(inventoryDto);
        InventoryMovement movement =
                InventoryMovementMapper.toInventoryMovementInitial(productId, totalQuantity);

        Integer inventoryId = inventoryRepository.save(inventory).getInventoryId();
        Integer inventoryMovementId = null;
        if (totalQuantity != 0)
            inventoryMovementId = movementRepository.save(movement).getInventoryMovementId();

        return new InventoryResult(inventoryId, inventoryMovementId);
    }

    //get inventory by id
    public InventoryDetailResponseDto getInventoryById(Integer inventoryId){
        InventoryValidation.inventoryIdValidation(inventoryId);

        Inventory inventory = inventoryRepository.findById(inventoryId)
                        .orElseThrow(InventoryNotFoundException::new);

        return InventoryMapper.inventoryToResponse(inventory);
    }

    //get inventory by product id
    public InventoryDetailResponseDto getInventoryByProductId(Integer productId){
        ProductValidation.productIdValidation(productId);

        if(!productRepository.existsById(productId))
            throw new ProductNotFoundException();

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(InventoryNotFoundForProductException::new);

        return InventoryMapper.inventoryToResponse(inventory);
    }

    //get all inventories - integration
    public List<InventoryDetailResponseDto> getAllInventories(){
        Iterable<Inventory> inventories = inventoryRepository.findAll();

        List<InventoryDetailResponseDto> result = new ArrayList<>();

        inventories.forEach(inv ->
                result.add(InventoryMapper.inventoryToResponse(inv)));

        return result;
    }

    //change total quantity
    @Transactional
    public InventoryResult changeTotalQuantity(
            Integer inventoryId, ChangeTotalQuantityDto changeDto)
    {
        InventoryValidation.inventoryIdValidation(inventoryId);

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(InventoryNotFoundException::new);

        int changeAmount = changeDto.getAmount();
        int totalQuantity = inventory.getTotalQuantity();
        //changeAmount can be + or -
        int newAmount = totalQuantity + changeAmount;
        if(newAmount < inventory.getReservedQuantity())
            throw new InvalidQuantityChangeException();

        inventoryRepository.changeTotalQuantity(inventoryId, newAmount);
        Integer movementId = movementRepository.save(
                InventoryMovementMapper.
                        toInventoryMovementQuantityChange(inventory.getProductId(), changeAmount))
                .getInventoryMovementId();

        return new InventoryResult(inventoryId, movementId);
    }

    //reservation
    @Transactional
    public InventoryResult reservation(Integer productId,
                                       ReservationChangeDto reserveDto)
    {
        ProductValidation.productIdValidation(productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(InventoryNotFoundForProductException::new);

        int reserveAmount = reserveDto.getReserveAmount();
        if(reserveAmount <= 0)
            throw new IllegalOrNullArgumentException();

        int reserved = inventory.getReservedQuantity();
        int availableAmount = inventory.getTotalQuantity() - reserved;
        if(availableAmount < reserveAmount)
            throw new InvalidReservationAmountException();

        int newReservedQuantity = reserved + reserveAmount;
        inventoryRepository.changeReservedQuantity(
                inventory.getInventoryId(), newReservedQuantity);

        Integer movementId = movementRepository.save(InventoryMovementMapper
                        .toInventoryMovementReservedChange(productId, reserveAmount))
                .getInventoryMovementId();

        return new InventoryResult(inventory.getInventoryId(), movementId);
    }

    //cancel reservation
    @Transactional
    public InventoryResult cancelReservation(Integer productId,
                                             ReservationChangeDto reserveDto)
    {
        ProductValidation.productIdValidation(productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(InventoryNotFoundForProductException::new);

        int cancelAmount = reserveDto.getReserveAmount();
        if(cancelAmount <= 0)
            throw new IllegalOrNullArgumentException();

        int reserved = inventory.getReservedQuantity();
        if(cancelAmount > reserved)
            throw new InvalidReservationAmountException();

        int newReservedQuantity = reserved - cancelAmount;
        inventoryRepository.changeReservedQuantity(inventory.getInventoryId(),
                newReservedQuantity);

        Integer movementId = movementRepository.save(InventoryMovementMapper
                .toInventoryMovementReservedChange(productId, -cancelAmount))
                .getInventoryMovementId();

        return new InventoryResult(inventory.getInventoryId(), movementId);
    }
}
