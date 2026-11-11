package com.example.order_flow.service;

import com.example.order_flow.dto.payment.CompleteOnlineRefundDto;
import com.example.order_flow.dto.refund.CreateRefundDto;
import com.example.order_flow.entity.Payment;
import com.example.order_flow.entity.Refund;
import com.example.order_flow.entity.enums.PaymentStatus;
import com.example.order_flow.entity.enums.RefundOutcome;
import com.example.order_flow.entity.enums.RefundStatus;
import com.example.order_flow.exceptions.IllegalOrNullArgumentException;
import com.example.order_flow.exceptions.PaymentNotFoundException;
import com.example.order_flow.exceptions.RefundException;
import com.example.order_flow.mapper.RefundMapper;
import com.example.order_flow.repository.PaymentRepository;
import com.example.order_flow.repository.RefundRepository;
import com.example.order_flow.utils.MockPaymentGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RefundPayService {
    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final MockPaymentGateway paymentGateway;
    private final Clock clock;

    @Autowired
    public RefundPayService(RefundRepository refundRepository,
                            PaymentRepository paymentRepository,
            MockPaymentGateway paymentGateway,Clock clock){
        this.refundRepository = refundRepository;
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
        this.clock = clock;
    }

    //create
    @Transactional
    public Integer createRefund(CreateRefundDto dto){
        if(dto == null)
            throw new RefundException("Refund Data can't be null");

        Integer paymentId = dto.getPaymentId();

        if(paymentId == null || paymentId < 6000)
            throw new IllegalOrNullArgumentException();
        if(dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new RefundException("Amount is negative!");

        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() ->
                new PaymentNotFoundException("Payment " + paymentId + " not found to refund"));

        if(payment.getStatus() != PaymentStatus.SUCCESS)
            throw new RefundException("Can't do refund for unsuccessful payment!");

        Optional<Refund> existRefund = refundRepository.findByPaymentId(paymentId);
        if(existRefund.isPresent())
            return existRefund.get().getRefundId();

        LocalDateTime createdAt = LocalDateTime.now(clock);

        return refundRepository.save(RefundMapper.createInitialRefund(dto, createdAt))
                .getRefundId();
    }

    //refund
    @Transactional
    public void processRefund(Integer refundId, RefundOutcome refundOutcome){
        if (refundId == null || refundId < 8000)
            throw new IllegalOrNullArgumentException();
        if(refundOutcome == null)
            throw new RefundException("Refound outcome can't be null!");

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RefundException("Refund not found"));

        if (refund.getStatus() != RefundStatus.PENDING)
            throw new RefundException("Refund is not pending");

        CompleteOnlineRefundDto result;

        if(refundOutcome == RefundOutcome.SUCCESS_REFUND_OPERATION)
            result = paymentGateway.simulateSuccessfulRefundPayment(refundId);
        else
            result = paymentGateway.simulateFailedRefundPayment(refundId);

        refundPaymentResult(result);
    }


    @Transactional
    public void refundPaymentResult(CompleteOnlineRefundDto dto){

        if(dto == null || dto.getRefundId() == null || dto.getRefundId() < 8000)
            throw new RefundException("Invalid refund result!");

        Integer refundId = dto.getRefundId();
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RefundException("Refund " + refundId + " Not found"));

        if (refund.getStatus() != RefundStatus.PENDING)
            throw new RefundException(
                    "Only pending refunds can receive a result!");

        int result;
        if(dto.isSuccess())
        {
            String reference = dto.getRefundReference();
            if (reference == null || reference.isBlank())
                throw new RefundException(
                        "Refund reference can't be empty!");
            LocalDateTime refundAt = LocalDateTime.now(clock);
            result = refundRepository.updatePendingRefundToSuccess(refundId, RefundStatus.SUCCESS,
                    refundAt, dto.getRefundReference());
            if(result != 1)
                throw new RefundException("Can't update refund " + refundId + " to successful!");
        }
        else{
            result = refundRepository.updatePendingRefundToFailed(refundId, RefundStatus.FAILED);
            if(result != 1)
                throw new RefundException("Can't update refund " + refundId + " to faild!");
        }
    }

    @Transactional
    public void retryRefund(Integer refundId, RefundOutcome refundOutcome){

        if (refundId == null || refundId < 8000)
            throw new IllegalOrNullArgumentException();
        if(refundOutcome == null)
            throw new RefundException("Refound outcome can't be null!");

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RefundException("Refund not found"));
        if (refund.getStatus() != RefundStatus.FAILED)
            throw new RefundException("Only failed refunds can be retried");

        int result = refundRepository.updateFailedRefundToPending(refundId, RefundStatus.PENDING);
        if (result != 1)
            throw new RefundException(
                    "Refund could not be prepared for retry!");

        CompleteOnlineRefundDto response;
        if(refundOutcome == RefundOutcome.SUCCESS_REFUND_OPERATION)
            response = paymentGateway.simulateSuccessfulRefundPayment(refundId);
        else
            response = paymentGateway.simulateFailedRefundPayment(refundId);
        refundPaymentResult(response);
    }
}
