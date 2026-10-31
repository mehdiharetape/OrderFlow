package com.example.order_flow.repository;

import com.example.order_flow.entity.InventoryMovement;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface InventoryMovementRepository
        extends CrudRepository<InventoryMovement, Integer>
{
    @Query("SELECT * FROM inventory_movement WHERE product_id = :productId")
    Iterable<InventoryMovement> findMovementsByProductId(Integer productId);

    @Query("SELECT * FROM inventory_movement WHERE order_id = :orderId")
    Iterable<InventoryMovement> findMovementsByOrderId(Integer orderId);

    Integer countByProductId(Integer productId);
}
