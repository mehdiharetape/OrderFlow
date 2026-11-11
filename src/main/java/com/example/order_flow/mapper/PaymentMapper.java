package com.example.order_flow.mapper;

import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.Payment;
import com.example.order_flow.entity.enums.PaymentStatus;

import java.time.LocalDateTime;

public class PaymentMapper {

    public static Payment createPrndingOrderPayment(Order order, LocalDateTime createdAt){
        Payment payment = new Payment();
        payment.setOrderId(order.getOrderId());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(createdAt);
        payment.setAmount(order.getTotalAmount());

        return payment;
    }
}
