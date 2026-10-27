package com.example.order_flow.service;

import com.example.order_flow.dto.order.OrderItemCreateDto;
import com.example.order_flow.dto.order.OrderItemRequestDto;
import com.example.order_flow.dto.order.OrderItemResponseDto;
import com.example.order_flow.entity.OrderItem;
import com.example.order_flow.exceptions.OrderNotFoundException;
import com.example.order_flow.exceptions.ProductNotFoundException;
import com.example.order_flow.mapper.OrderItemMapper;
import com.example.order_flow.repository.OrderItemRepository;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.validation.OrderItemValidation;
import com.example.order_flow.validation.OrderValidation;
import com.example.order_flow.validation.ProductValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository,
                            ProductRepository productRepository,
                            OrderRepository orderRepository)
    {
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    //create order item
    @Transactional
    public List<Integer> createOrderItems(Integer orderId ,List<OrderItemRequestDto> requestsDto)
    {
        OrderItemValidation.createOrderItemValidation(orderId, requestsDto);

        if (!orderRepository.existsById(orderId))
            throw new OrderNotFoundException(orderId);

        List<Integer> itemIds = new ArrayList<>();
        for (OrderItemRequestDto request : requestsDto)
        {
            BigDecimal unitPrice = productRepository.findById(request.getProductId())
                    .orElseThrow(ProductNotFoundException::new)
                    .getCurrentPrice();

            OrderItemCreateDto createDto = new OrderItemCreateDto();
            createDto.setOrderId(orderId);
            createDto.setProductId(request.getProductId());
            createDto.setQuantity(request.getQuantity());
            createDto.setUnitPrice(unitPrice);

            Integer id = orderItemRepository
                    .save(OrderItemMapper.createInitialOrderItem(createDto))
                    .getOrderItemId();
            itemIds.add(id);
        }

        return itemIds;
    }

    //get all order items **
    public List<OrderItemResponseDto> getAllOrderItems(){
        Iterable<OrderItem> items = orderItemRepository.findAll();
        List<OrderItemResponseDto> result = new ArrayList<>();
        items.forEach(i -> result.add(OrderItemMapper.orderITemToResponse(i)));

        return result;
    }

    //get order items by order id **
    public List<OrderItemResponseDto> getAllOrderItemsByOrderId(Integer orderId){
        OrderValidation.orderIdValidation(orderId);
        Iterable<OrderItem> items = orderItemRepository.findAllByOrderId(orderId);
        List<OrderItemResponseDto> result = new ArrayList<>();
        items.forEach(i -> result.add(OrderItemMapper.orderITemToResponse(i)));

        return result;
    }

    //get order items by product id **
    public List<OrderItemResponseDto> getAllOrderItemsByProductId(Integer productId){
        ProductValidation.productIdValidation(productId);
        Iterable<OrderItem> items = orderItemRepository.findAllByProductId(productId);
        List<OrderItemResponseDto> result = new ArrayList<>();
        items.forEach(i -> result.add(OrderItemMapper.orderITemToResponse(i)));

        return result;
    }
}
