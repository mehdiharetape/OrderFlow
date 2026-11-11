package com.example.order_flow.service;

import com.example.order_flow.dto.payment.CompleteOnlinePaymentDto;
import com.example.order_flow.dto.payment.CompleteOnlineRefundDto;
import com.example.order_flow.dto.refund.CreateRefundDto;
import com.example.order_flow.entity.Order;
import com.example.order_flow.entity.Payment;
import com.example.order_flow.entity.enums.OrderStatus;
import com.example.order_flow.entity.enums.PaymentStatus;
import com.example.order_flow.entity.enums.RefundOutcome;
import com.example.order_flow.exceptions.*;
import com.example.order_flow.mapper.PaymentMapper;
import com.example.order_flow.repository.OrderRepository;
import com.example.order_flow.repository.PaymentRepository;
import com.example.order_flow.repository.RefundRepository;
import com.example.order_flow.service.interfaces.PaymentService;
import com.example.order_flow.utils.MockPaymentGateway;
import com.example.order_flow.utils.records.PaymentResult;
import com.example.order_flow.validation.OrderValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class OnlinePaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RefundPayService refundPayService;
    private final Clock clock;

    @Autowired
    public OnlinePaymentService(PaymentRepository paymentRepository,
                                OrderRepository orderRepository,
                                RefundPayService refundPayService,
                                Clock clock)
    {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.refundPayService = refundPayService;
        this.clock = clock;
    }

    //pay order
    @Transactional
    public Integer createPayOrder(Integer orderId)
    {
        OrderValidation.orderIdValidation(orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new PayOrderFailException("Can't pay order '" +
                    orderId + "' with status " + order.getStatus());
        }

        LocalDateTime createdAt = LocalDateTime.now(clock);
        return paymentRepository.save(PaymentMapper
                .createPrndingOrderPayment(order, createdAt)).getPaymentId();

    }

    //complete payment
    @Transactional
    public PaymentResult processPaymentResult(CompleteOnlinePaymentDto paymentDto)
    {
        if (paymentDto == null)
            throw new IllegalOrNullArgumentException();
        Integer paymentId = paymentDto.getPaymentId();
        boolean isSuccess = paymentDto.getSuccess();
        int payResult;
        int orderResult;

        if (paymentId == null || paymentId < 6000)
            throw new IllegalOrNullArgumentException();

        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new PaymentNotFoundException("Payment '" + paymentId +"' not found"));
        if(payment.getStatus() != PaymentStatus.PENDING)
            throw new PayOrderFailException("Can't complete payment " + paymentId);

        if (!isSuccess)
        {
            int resultFail = paymentRepository.updatePendingPayment(paymentId, PaymentStatus.FAIL,
                    null, null);

            if (resultFail != 1)
                throw new PayOrderFailException("Payment could not be updated");

            return new PaymentResult(paymentId, null);
        }
        if(paymentDto.getTransactionalReference() == null ||
                paymentDto.getTransactionalReference().isBlank()) {
            throw new PayOrderFailException("Transaction Reference Can't be empty for successful payment!");
        }

        LocalDateTime paidAt = LocalDateTime.now(clock);
        Integer orderId = payment.getOrderId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        //----OrderStatus.PENDING_PAYMENT
        if(order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            payResult = paymentRepository.updatePendingPayment(paymentId, PaymentStatus.SUCCESS,
                    paidAt, paymentDto.getTransactionalReference());

            if (payResult != 1)
                throw new PayOrderFailException("Payment could not be updated");

            CreateRefundDto dto = new CreateRefundDto();
            dto.setPaymentId(paymentId);
            dto.setAmount(payment.getAmount());

            Integer refundId = refundPayService.createRefund(dto);
            refundPayService.processRefund(refundId, RefundOutcome.SUCCESS_REFUND_OPERATION);
            return new PaymentResult(paymentId, paymentDto.getTransactionalReference());
        }

        //----order status = PENDING_PAYMENT
        payResult = paymentRepository.updatePendingPayment(paymentId, PaymentStatus.SUCCESS,
                paidAt, paymentDto.getTransactionalReference());
        orderResult = orderRepository.changeOrderStatus(orderId, OrderStatus.PAID);

        if (payResult != 1)
            throw new PayOrderFailException("Payment could not be updated");
        if (orderResult != 1)
            throw new PayOrderFailException("Order could not be updated");

        return new PaymentResult(paymentId, paymentDto.getTransactionalReference());
    }
}
