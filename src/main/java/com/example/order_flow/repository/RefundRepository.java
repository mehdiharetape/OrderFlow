package com.example.order_flow.repository;

import com.example.order_flow.entity.Refund;
import com.example.order_flow.entity.enums.RefundStatus;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefundRepository
        extends CrudRepository<Refund, Integer>
{
    boolean existsByPaymentId(Integer paymentId);

    Optional<Refund> findByPaymentId(Integer paymentId);

    @Modifying
    @Query("""
    UPDATE refund
    SET status = :status,
        refund_at = :refundAt,
        refund_reference = :reference
    WHERE refund_id = :refundId
      AND status = 'PENDING'
    """)
    int updatePendingRefundToSuccess(
            Integer refundId,
            RefundStatus status,
            LocalDateTime refundAt,
            String reference);


    @Modifying
    @Query("""
    UPDATE refund
    SET status = :status
    WHERE refund_id = :refundId
      AND status = 'PENDING'
    """)
    int updatePendingRefundToFailed(
            Integer refundId,
            RefundStatus status);


    @Modifying
    @Query("""
    UPDATE refund
    SET status = :status
    WHERE refund_id = :refundId
      AND status = 'FAILED'
    """)
    int updateFailedRefundToPending(
            Integer refundId,
            RefundStatus status);

    @Modifying
    @Query("UPDATE refund SET status = :status WHERE refund_id = :refundId")
    int changeRefundStatus(Integer refundId, RefundStatus status);
}
