package com.example.order_flow.dto.inventory;

public class ReservationChangeDto {
    private Integer orderId;
    private Integer reserveAmount;

    public Integer getReserveAmount(){
        return reserveAmount;
    }

    public Integer getOrderId(){
        return orderId;
    }

    public void setReserveAmount(Integer reserveAmount){
        this.reserveAmount = reserveAmount;
    }

    public void setOrderId(Integer orderId){
        this.orderId = orderId;
    }
}
