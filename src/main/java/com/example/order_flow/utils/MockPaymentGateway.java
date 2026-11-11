package com.example.order_flow.utils;
import com.example.order_flow.dto.payment.CompleteOnlinePaymentDto;
import com.example.order_flow.dto.payment.CompleteOnlineRefundDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MockPaymentGateway
{
    //payment
    public CompleteOnlinePaymentDto simulateSuccessfulPayment(Integer paymentId){
        var result = new CompleteOnlinePaymentDto();
        result.setSuccess(true);
        result.setTransactionalReference("PAYMENT-SUCCESS-"+UUID.randomUUID());
        result.setPaymentId(paymentId);

        return result;
    }

    public CompleteOnlinePaymentDto simulateFailedPayment(Integer paymentId){
        var result = new CompleteOnlinePaymentDto();
        result.setSuccess(false);
        result.setTransactionalReference(null);
        result.setPaymentId(paymentId);

        return result;
    }

    //refund
    public CompleteOnlineRefundDto
            simulateSuccessfulRefundPayment(Integer refundId){
        var result = new CompleteOnlineRefundDto();
        result.setSuccess(true);
        result.setRefundReference("REFUNDED-SUCCESS-"+ UUID.randomUUID());
        result.setRefundId(refundId);

        return result;
    }

    public CompleteOnlineRefundDto
            simulateFailedRefundPayment(Integer refundId){
        var result = new CompleteOnlineRefundDto();
        result.setSuccess(false);
        result.setRefundReference(null);
        result.setRefundId(refundId);

        return result;
    }
}
