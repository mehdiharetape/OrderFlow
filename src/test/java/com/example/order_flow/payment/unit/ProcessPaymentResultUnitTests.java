package com.example.order_flow.payment.unit;

import com.example.order_flow.dto.payment.CompleteOnlinePaymentDto;
import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.Payment;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.entity.enums.PaymentStatus;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.OrderNotFoundException;
import com.example.order_flow.exceptions.PayOrderFailException;
import com.example.order_flow.exceptions.PaymentNotFoundException;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.repository.PaymentRepository;
import com.example.order_flow.repository.RefundRepository;
import com.example.order_flow.service.OnlinePaymentService;
import com.example.order_flow.service.RefundPayService;
import com.example.order_flow.utils.MockPaymentGateway;
import com.example.order_flow.utils.records.PaymentResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProcessPaymentResultUnitTests {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private RefundPayService refundPayService;

    private OnlinePaymentService paymentService;
    private Clock clock = Clock.fixed(
            Instant.parse("2026-11-09T16:00:00Z"),
            ZoneOffset.UTC
    );

    @BeforeEach
    void setUp(){
        paymentService = new OnlinePaymentService(paymentRepository,
                orderRepository,refundPayService,clock);
    }

    @Test
    public void processPaymentResultHappyFlow(){
        Integer paymentId = 6001;
        Integer orderId = 3001;

        CompleteOnlinePaymentDto dto = new CompleteOnlinePaymentDto();
        dto.setPaymentId(paymentId);
        dto.setSuccess(true);
        dto.setTransactionalReference("XXX-123-YYY");

        Payment payment = new Payment(paymentId);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setOrderId(orderId);

        Order order = new Order(orderId);
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        given(paymentRepository.updatePendingPayment(anyInt(), any(PaymentStatus.class),
                any(LocalDateTime.class), any(String.class))).willReturn(1);
        given(orderRepository.changeOrderStatus(anyInt(), any(OrderStatus.class)))
                .willReturn(1);

        //act
        PaymentResult result = paymentService.processPaymentResult(dto);

        //asserts
        assertEquals(paymentId, result.paymentId());
        assertEquals(dto.getTransactionalReference(),
                result.transactionalReference());

        verify(paymentRepository).updatePendingPayment(paymentId, PaymentStatus.SUCCESS,
                LocalDateTime.now(clock), dto.getTransactionalReference());
        verify(orderRepository).changeOrderStatus(orderId, OrderStatus.PAID);
    }

    //wrong payment status
    @Test public void wrongPaymentStatusTest(){
        CompleteOnlinePaymentDto dto = new CompleteOnlinePaymentDto();
        dto.setPaymentId(6001);

        Payment payment = new Payment(6001);
        payment.setStatus(PaymentStatus.FAIL);
        payment.setOrderId(3001);

        given(paymentRepository.findById(6001))
                .willReturn(Optional.of(payment));

        assertThrows(PayOrderFailException.class,
                () -> paymentService.processPaymentResult(dto));

        verify(paymentRepository, never()).updatePendingPayment(anyInt(), any(PaymentStatus.class),
                any(LocalDateTime.class), anyString());
        verify(orderRepository, never()).changeOrderStatus(anyInt(), any(OrderStatus.class));
    }

    //wrong order status
    @Test
    public void wrongOrderStatusTest(){
        Integer paymentId = 6001;
        Integer orderId = 3001;

        CompleteOnlinePaymentDto dto = new CompleteOnlinePaymentDto();
        dto.setPaymentId(paymentId);
        dto.setSuccess(true);
        dto.setTransactionalReference("XXX-123-YYY");

        Payment payment = new Payment(paymentId);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setOrderId(orderId);

        Order order = new Order(orderId);
        order.setStatus(OrderStatus.PAID);

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        //act
        assertThrows(PayOrderFailException.class,
                () -> paymentService.processPaymentResult(dto));

        verify(paymentRepository, never()).updatePendingPayment(anyInt(), any(PaymentStatus.class),
                any(LocalDateTime.class), anyString());
        verify(orderRepository, never()).changeOrderStatus(anyInt(), any(OrderStatus.class));
    }

    //fail payment
    @Test
    public void failProcessPaymentTest(){
        CompleteOnlinePaymentDto dto = new CompleteOnlinePaymentDto();
        dto.setSuccess(false);
        dto.setPaymentId(6001);

        Payment payment = new Payment(6001);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setOrderId(3001);

        given(paymentRepository.findById(6001))
                .willReturn(Optional.of(payment));

        PaymentResult result = paymentService.processPaymentResult(dto);

        assertEquals(6001, result.paymentId());
        assertNull(result.transactionalReference());

        verify(paymentRepository).updatePendingPayment(6001, PaymentStatus.FAIL,
                null, null);
        verify(orderRepository, never()).changeOrderStatus(anyInt(), any(OrderStatus.class));
    }

    //payment not found
    @Test
    public void paymentNotFoundTest(){
        CompleteOnlinePaymentDto dto = new CompleteOnlinePaymentDto();
        dto.setSuccess(false);
        dto.setPaymentId(6100);

        given(paymentRepository.findById(6100))
                .willReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class,
                () -> paymentService.processPaymentResult(dto));

        verify(paymentRepository, never()).updatePendingPayment(anyInt(), any(PaymentStatus.class),
                any(LocalDateTime.class), anyString());
        verify(orderRepository, never()).changeOrderStatus(anyInt(), any(OrderStatus.class));
    }

    //order not found
    @Test
    public void orderNotFoundTest(){
        Integer paymentId = 6001;
        Integer orderId = 3100;

        CompleteOnlinePaymentDto dto = new CompleteOnlinePaymentDto();
        dto.setPaymentId(paymentId);
        dto.setSuccess(true);
        dto.setTransactionalReference("XXX-123-YYY");

        Payment payment = new Payment(paymentId);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setOrderId(orderId);

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        given(orderRepository.findById(orderId)).willReturn(Optional.empty());


        //act
        assertThrows(OrderNotFoundException.class,
                () -> paymentService.processPaymentResult(dto));

        verify(paymentRepository, never()).updatePendingPayment(paymentId, PaymentStatus.SUCCESS,
                LocalDateTime.now(clock), dto.getTransactionalReference());
        verify(orderRepository, never()).changeOrderStatus(orderId, OrderStatus.PAID);
    }

    //null or invalid payment id
    @Test
    public void nullPaymentIdTest(){
        CompleteOnlinePaymentDto dto = new CompleteOnlinePaymentDto();

        assertThrows(IllegalOrNullArgumentException.class,
                () -> paymentService.processPaymentResult(dto));

        verify(paymentRepository, never()).updatePendingPayment(anyInt(), any(PaymentStatus.class),
                any(LocalDateTime.class), anyString());
        verify(orderRepository, never()).changeOrderStatus(anyInt(), any(OrderStatus.class));
    }

    @Test
    public void invalidPaymentIdTest(){
        CompleteOnlinePaymentDto dto = new CompleteOnlinePaymentDto();
        dto.setPaymentId(5999);

        assertThrows(IllegalOrNullArgumentException.class,
                () -> paymentService.processPaymentResult(dto));

        verify(paymentRepository, never()).updatePendingPayment(anyInt(), any(PaymentStatus.class),
                any(LocalDateTime.class), anyString());
        verify(orderRepository, never()).changeOrderStatus(anyInt(), any(OrderStatus.class));
    }
}
