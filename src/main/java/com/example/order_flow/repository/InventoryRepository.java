package com.example.order_flow.repository;

import com.example.order_flow.entity.Inventory;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InventoryRepository
        extends CrudRepository<Inventory, Integer>
{

    boolean existsByProductId(Integer productId);
    Optional<Inventory> findByProductId(Integer productId);

    @Modifying
    @Query("UPDATE inventory SET total_quantity = :newQuantity" +
            " WHERE inventory_id = :inventoryId")
    void changeTotalQuantity(Integer inventoryId, Integer newQuantity);

    @Modifying
    @Query("UPDATE inventory SET reserved_quantity = :newReservedQuantity" +
            " WHERE inventory_id = :inventoryId")
    void changeReservedQuantity(Integer inventoryId, Integer newReservedQuantity);
}
