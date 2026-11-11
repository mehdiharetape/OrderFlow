package com.example.order_flow.payment.integration;

import com.example.order_flow.entity.Payment;
import com.example.order_flow.entity.enums.PaymentStatus;
import com.example.order_flow.exceptions.OrderNotFoundException;
import com.example.order_flow.exceptions.PayOrderFailException;
import com.example.order_flow.repository.PaymentRepository;
import com.example.order_flow.service.OnlinePaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CreatePayOrderIntegrationTests {
    @Autowired
    private OnlinePaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;

    //@Test
    public void createPaymentHappyFlow(){
        Integer orderId = 3050;

        Integer paymentId = paymentService.createPayOrder(orderId);
        Optional<Payment> payment = paymentRepository.findById(paymentId);

        assertEquals(paymentId, payment.get().getPaymentId());
        assertEquals(PaymentStatus.PENDING, payment.get().getStatus());
        assertEquals(3050, payment.get().getOrderId());
        assertNull(payment.get().getPaidAt());
        assertNull(payment.get().getTransactionReference());
    }

    @Test
    public void orderNotFoundIntegrationTest(){
        Integer orderId = 3500;

        assertThrows(OrderNotFoundException.class,
                () -> paymentService.createPayOrder(orderId));
    }

    @Test
    public void wrongOrderStatusWhenCreatePaymentIntegrationTest(){
        Integer orderId = 3042;

        assertThrows(PayOrderFailException.class,
                () -> paymentService.createPayOrder(orderId));
    }
}
