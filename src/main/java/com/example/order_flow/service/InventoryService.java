package com.example.order_flow.service;

import com.example.order_flow.dto.inventory.ChangeTotalQuantityDto;
import com.example.order_flow.dto.inventory.CreateInventoryDto;
import com.example.order_flow.dto.inventory.InventoryDetailResponseDto;
import com.example.order_flow.dto.inventory.ReservationChangeDto;
import com.example.order_flow.dto.movement.CreateMovementDto;
import com.example.order_flow.entity.Inventory;
import com.example.order_flow.entity.enums.MovementReason;
import com.example.order_flow.exceptions.*;
import com.example.order_flow.mapper.InventoryMapper;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.utils.InventoryResult;
import com.example.order_flow.validation.InventoryValidation;
import com.example.order_flow.validation.OrderValidation;
import com.example.order_flow.validation.ProductValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryMovementService movementService;
    private final OrderRepository orderRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository,
                            ProductRepository productRepository,
                            InventoryMovementService movementService,
                            OrderRepository orderRepository)
    {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.movementService = movementService;
        this.orderRepository = orderRepository;
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

        Integer inventoryId = inventoryRepository.save(inventory).getInventoryId();

        Integer movementId = null;
        if(totalQuantity != 0){
            CreateMovementDto dto = new CreateMovementDto();
            dto.setOrderId(null);
            dto.setProductId(productId);
            dto.setPhysicalChange(totalQuantity);
            dto.setReservedChange(0);
            dto.setCreatedAt(LocalDateTime.now());
            dto.setReason(MovementReason.INITIAL_STOCK);
            movementId = movementService.createInventoryMovement(dto);
        }

        return new InventoryResult(inventoryId, movementId);
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
            Integer productId, ChangeTotalQuantityDto changeDto)
    {
        ProductValidation.productIdValidation(productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(InventoryNotFoundForProductException::new);

        int changeAmount = changeDto.getAmount();
        int totalQuantity = inventory.getTotalQuantity();
        //changeAmount can be + or -
        int newAmount = totalQuantity + changeAmount;
        if(newAmount < inventory.getReservedQuantity())
            throw new InvalidQuantityChangeException();

        inventoryRepository.changeTotalQuantity(inventory.getInventoryId(), newAmount);

        CreateMovementDto dto = new CreateMovementDto();
        dto.setOrderId(null);
        dto.setProductId(productId);
        dto.setPhysicalChange(changeAmount);
        dto.setReservedChange(0);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setReason(MovementReason.CHANGE_TOTAL_QUANTITY);
        Integer movementId = movementService.createInventoryMovement(dto);

        return new InventoryResult(inventory.getInventoryId(), movementId);
    }

    //reservation - ****
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

        CreateMovementDto dto = new CreateMovementDto();
        dto.setOrderId(reserveDto.getOrderId());
        dto.setProductId(productId);
        dto.setPhysicalChange(0);
        dto.setReservedChange(reserveAmount);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setReason(MovementReason.RESERVATION);
        Integer movementId = movementService.createInventoryMovement(dto);

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

        CreateMovementDto dto = new CreateMovementDto();
        dto.setOrderId(reserveDto.getOrderId());
        dto.setProductId(productId);
        dto.setPhysicalChange(0);
        dto.setReservedChange(-cancelAmount);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setReason(MovementReason.CANCEL_RESERVATION);
        Integer movementId = movementService.createInventoryMovement(dto);

        return new InventoryResult(inventory.getInventoryId(), movementId);
    }
}
