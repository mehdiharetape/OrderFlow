package com.example.order_flow.payment.integration;

import com.example.order_flow.dto.payment.CompleteOnlinePaymentDto;
import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.Payment;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.entity.enums.PaymentStatus;
import com.example.order_flow.exceptions.PayOrderFailException;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.repository.PaymentRepository;
import com.example.order_flow.service.OnlinePaymentService;
import com.example.order_flow.utils.records.PaymentResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProcessPaymentResultIntegrationTests {
    @Autowired
    private OnlinePaymentService paymentService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    //@Test
    public void processPaymentHappyFlow(){
        Integer paymentId = 6000;

        CompleteOnlinePaymentDto dto = new CompleteOnlinePaymentDto();
        dto.setPaymentId(paymentId);
        dto.setSuccess(true);
        dto.setTransactionalReference("XXX-123-PPP");

        PaymentResult result = paymentService.processPaymentResult(dto);
        Optional<Payment> payment = paymentRepository.findById(paymentId);
        Optional<Order> order = orderRepository.findById(payment.get().getOrderId());

        assertEquals(paymentId, result.paymentId());
        assertEquals(dto.getTransactionalReference(), result.transactionalReference());

        assertEquals(PaymentStatus.SUCCESS, payment.get().getStatus());
        assertEquals(OrderStatus.PAID, order.get().getStatus());
    }

    //@Test
    public void failedProcessPaymentTest(){
        Integer paymentId = 6001;

        CompleteOnlinePaymentDto dto = new CompleteOnlinePaymentDto();
        dto.setPaymentId(paymentId);
        dto.setSuccess(false);
        dto.setTransactionalReference(null);

        PaymentResult result = paymentService.processPaymentResult(dto);
        Optional<Payment> payment = paymentRepository.findById(paymentId);
        Optional<Order> order = orderRepository.findById(payment.get().getOrderId());

        assertEquals(paymentId, result.paymentId());
        assertNull(result.transactionalReference());

        assertEquals(PaymentStatus.FAIL, payment.get().getStatus());
        assertEquals(OrderStatus.PENDING_PAYMENT, order.get().getStatus());
    }

    //wrong payment status
    @Test
    public void wrongPaymentStatusTest(){
        Integer paymentId = 6001;
        CompleteOnlinePaymentDto dto = new CompleteOnlinePaymentDto();
        dto.setPaymentId(paymentId);

        assertThrows(PayOrderFailException.class,
                () -> paymentService.processPaymentResult(dto));
    }

    //wrong order status
    @Test //payment is PENDING but order has been EXPIRED
    public void wrongOrderStatusTest(){
        Integer paymentId = 6002;

        CompleteOnlinePaymentDto dto = new CompleteOnlinePaymentDto();
        dto.setSuccess(true);
        dto.setPaymentId(paymentId);
        dto.setTransactionalReference("XXX-PPPP-1010");

        assertThrows(PayOrderFailException.class,
                () -> paymentService.processPaymentResult(dto));
    }

    //payment not found
    //order not found
}
