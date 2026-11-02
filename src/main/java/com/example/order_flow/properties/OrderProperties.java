package com.example.order_flow.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "order")
public class OrderProperties {

    private Integer paymentExpirationMinutes;

    public Integer getPaymentExpirationMinutes(){
        return paymentExpirationMinutes;
    }

    public void setPaymentExpirationMinutes(Integer paymentExpirationMinutes){
        this.paymentExpirationMinutes=  paymentExpirationMinutes;
    }
}
