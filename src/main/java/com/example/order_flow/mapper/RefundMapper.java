package com.example.order_flow.mapper;

import com.example.order_flow.dto.refund.CreateRefundDto;
import com.example.order_flow.entity.Refund;
import com.example.order_flow.entity.enums.RefundStatus;

import java.time.LocalDateTime;

public class RefundMapper {
    public static Refund createInitialRefund(CreateRefundDto dto, LocalDateTime createdAt){
        Refund refund = new Refund();
        refund.setPaymentId(dto.getPaymentId());
        refund.setAmount(dto.getAmount());
        refund.setStatus(RefundStatus.PENDING);
        refund.setCreatedAt(createdAt);
        refund.setRefundAt(null);
        refund.setAttemptCount(0);
        refund.setRefundReference(null);

        return refund;
    }
}
