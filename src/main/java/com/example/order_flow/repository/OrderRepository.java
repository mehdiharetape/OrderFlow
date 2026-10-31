package com.example.order_flow.repository;

import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.enums.OrderStatus;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Spliterator;

public interface OrderRepository
        extends CrudRepository<Order, Integer>
{
    Iterable<Order> findAllByStatus(OrderStatus status);

    @Modifying
    @Query("UPDATE `order` SET status = :orderStatus WHERE order_id = :orderId")
    void changeOrderStatus(Integer orderId, OrderStatus orderStatus);
}
