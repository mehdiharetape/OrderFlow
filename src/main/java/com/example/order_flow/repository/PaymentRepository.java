package com.example.order_flow.repository;

import com.example.order_flow.entity.Payment;
import com.example.order_flow.entity.enums.PaymentStatus;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface PaymentRepository extends
        CrudRepository<Payment, Integer>
{
    @Modifying
    @Query("UPDATE payment SET status = :status, paid_at = :paidAt, " +
            "transaction_reference = :transactionReference WHERE payment_id = :paymentId" +
            "  AND status = 'PENDING'")
    int updatePendingPayment(Integer paymentId, PaymentStatus status, LocalDateTime paidAt,
                              String transactionReference);
}
