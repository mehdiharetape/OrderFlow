package com.example.order_flow.service;

import com.example.order_flow.dto.inventory.ReservationChangeDto;
import com.example.order_flow.dto.order.OrderBriefResponseDto;
import com.example.order_flow.dto.order.OrderItemRequestDto;
import com.example.order_flow.dto.order.OrderItemResponseDto;
import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.OrderItem;
import com.example.order_flow.entity.Product;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.entity.enums.ProductStatus;
import com.example.order_flow.exceptions.*;
import com.example.order_flow.mapper.OrderItemMapper;
import com.example.order_flow.mapper.OrderMapper;
import com.example.order_flow.repository.InventoryRepository;
import com.example.order_flow.repository.OrderItemRepository;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.repository.ProductRepository;
import com.example.order_flow.utils.OrderResultById;
import com.example.order_flow.validation.OrderValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final OrderItemService orderItemService;
    private final InventoryRepository inventoryRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        InventoryService inventoryService,
                        OrderItemService orderItemService,
                        InventoryRepository inventoryRepository, OrderItemRepository orderItemRepository)
    {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
        this.orderItemService = orderItemService;
        this.inventoryRepository = inventoryRepository;
        this.orderItemRepository = orderItemRepository;
    }

    //create order
    @Transactional
    public Order createOrder(List<OrderItemRequestDto> orderItems)
    {
        BigDecimal totalAmount = BigDecimal.ZERO;
        OrderValidation.orderCreateValidation(orderItems);

        for (OrderItemRequestDto item : orderItems)
        {
            Product p = productRepository.findById(item.getProductId())
                    .orElseThrow(ProductNotFoundException::new);

            if(!inventoryRepository.existsByProductId(p.getProductId()))
                throw new InventoryNotFoundForProductException();

            if (p.getStatus() == ProductStatus.INACTIVE)
                throw new InactiveProductException(item.getProductId());

            totalAmount = totalAmount.add(p.getCurrentPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        Order order = orderRepository
                .save(OrderMapper.createInitialOrderToOrder(totalAmount));
        orderItemService.createOrderItems(order.getOrderId(), orderItems);

        for (OrderItemRequestDto item : orderItems){
            var reserveDto = new ReservationChangeDto();
            reserveDto.setOrderId(order.getOrderId());
            reserveDto.setReserveAmount(item.getQuantity());
            inventoryService.reservation(item.getProductId(), reserveDto);
        }

        return order;
    }

    //cancel order
    @Transactional
    public Integer cancelOrder(Integer orderId)
    {
        OrderValidation.orderIdValidation(orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if(order.getStatus() != OrderStatus.PENDING_PAYMENT)
            throw new OrderCancelFailException("can't cancel order " + orderId);

        List<OrderItem> orderItems = StreamSupport
                .stream(orderItemRepository.findAllByOrderId(orderId).
                        spliterator(),false).toList();

        if(orderItems.isEmpty())
            throw new OrderCancelFailException("order " + orderId +" has no items");

        for (OrderItem item : orderItems)
        {
            ReservationChangeDto changeDto = new ReservationChangeDto();
            changeDto.setOrderId(orderId);
            changeDto.setReserveAmount(item.getQuantity());
            inventoryService.cancelReservation(item.getProductId(), changeDto);
        }

        orderRepository.changeOrderStatus(orderId, OrderStatus.CANCELED);

        return orderId;
    }

    //expire unpaid order

    //get all orders **
    public List<OrderBriefResponseDto> getAllOrders(){
        Iterable<Order> orders = orderRepository.findAll();

        List<OrderBriefResponseDto> result = new ArrayList<>();
        orders.forEach(o -> result.add(OrderMapper.orderToBriefResponse(o)));

        return result;
    }

    //get orders by order id + order item **
    public OrderResultById getOrderDetailById(Integer orderId)
    {
        OrderValidation.orderIdValidation(orderId);

        OrderBriefResponseDto order = OrderMapper.
                orderToBriefResponse(orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId)));

        List<OrderItemResponseDto> orderItems = StreamSupport
                        .stream(orderItemRepository.findAllByOrderId(orderId).spliterator(),
                                false).map(OrderItemMapper::orderITemToResponse).toList();

        return new OrderResultById(order, orderItems);
    }

    //get orders by order status
    public List<OrderBriefResponseDto> getOrderByStatus(OrderStatus status){
        if (status == null)
            throw new IllegalOrNullArgumentException();

        return StreamSupport.stream(orderRepository.findAllByStatus(status)
                        .spliterator(),false)
                .map(OrderMapper::orderToBriefResponse).toList();
    }
}
