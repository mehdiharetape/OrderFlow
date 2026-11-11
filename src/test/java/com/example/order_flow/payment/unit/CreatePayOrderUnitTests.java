package com.example.order_flow.payment.unit;

import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.Payment;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.OrderNotFoundException;
import com.example.order_flow.exceptions.PayOrderFailException;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.repository.PaymentRepository;
import com.example.order_flow.repository.RefundRepository;
import com.example.order_flow.service.OnlinePaymentService;
import com.example.order_flow.service.RefundPayService;
import com.example.order_flow.utils.MockPaymentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreatePayOrderUnitTests {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private RefundPayService refundPayService;

    private OnlinePaymentService paymentService;
    private Clock clock = Clock.fixed(
            Instant.parse("2026-11-08T15:00:00Z"),
            ZoneOffset.UTC
    );

    @BeforeEach
    void setUp(){
        paymentService = new OnlinePaymentService(paymentRepository,
                orderRepository,refundPayService,clock);
    }

    @Test
    public void createPaymentHappyFlow(){
        Integer orderId = 3042;
        Order order = new Order(orderId);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setExpireAt(LocalDateTime.parse("2026-11-09T15:00:00"));

        given(orderRepository.findById(orderId))
                .willReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> {
                    Payment payment = invocation.getArgument(0);
                    payment.setPaymentId(6001);
                    return payment;
                });

        //act
        Integer paymentId = paymentService.createPayOrder(orderId);

        assertEquals(6001, paymentId);

        verify(orderRepository).findById(orderId);
        verify(paymentRepository).save(any(Payment.class));
    }

    //wrong order status
    @Test
    public void wrongOrderStatusTest(){
        Integer orderId = 3042;
        Order order = new Order(orderId);
        order.setStatus(OrderStatus.EXPIRED);
        order.setExpireAt(LocalDateTime.parse("2026-11-07T15:00:00"));

        given(orderRepository.findById(orderId))
                .willReturn(Optional.of(order));

        //act
        assertThrows(PayOrderFailException.class,
                () -> paymentService.createPayOrder(orderId));

        verify(orderRepository).findById(orderId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    //null or invalid order id
    @Test
    public void invalidOrderIdTest(){
        //act
        assertThrows(IllegalOrNullArgumentException.class,
                () -> paymentService.createPayOrder(2999));

        verify(orderRepository, never()).findById(anyInt());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    public void nullOrderIdTest(){
        //act
        assertThrows(IllegalOrNullArgumentException.class,
                () -> paymentService.createPayOrder(null));

        verify(orderRepository, never()).findById(anyInt());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    //order not found
    @Test
    public void orderNotFoundTest(){
        Integer orderId = 3200;

        given(orderRepository.findById(orderId))
                .willReturn(Optional.empty());

        //act
        assertThrows(OrderNotFoundException.class,
                () -> paymentService.createPayOrder(orderId));

        verify(orderRepository).findById(orderId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
