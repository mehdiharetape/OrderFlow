package com.example.order_flow.repository;

import com.example.order_flow.entity.Product;
import com.example.order_flow.entity.enums.ProductStatus;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductRepository extends
        CrudRepository<Product, Integer>
{
    boolean existsByName(String name);

    @Modifying
    @Query("UPDATE product SET name = :name, current_price = :price" +
            " WHERE product_id = :productId")
    void updateProductNamePrice(Integer productId, String name, BigDecimal price);

    @Modifying
    @Query("UPDATE product SET status = :newStatus WHERE product_id = :productId")
    void updateProductStatus(Integer productId, ProductStatus newStatus);
}
