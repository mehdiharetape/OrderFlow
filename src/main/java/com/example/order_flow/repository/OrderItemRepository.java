package com.example.order_flow.repository;

import com.example.order_flow.entity.OrderItem;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository
        extends CrudRepository<OrderItem, Integer>
{
    @Query("SELECT * FROM order_item WHERE order_id = :orderId")
    Iterable<OrderItem> findAllByOrderId(Integer orderId);

    @Query("SELECT * FROM order_item WHERE product_id = :productId")
    Iterable<OrderItem> findAllByProductId(Integer productId);
}
