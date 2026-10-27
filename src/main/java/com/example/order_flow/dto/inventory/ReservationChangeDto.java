package com.example.order_flow.dto.inventory;

public class ReservationChangeDto {
    private Integer reserveAmount;

    public int getReserveAmount(){
        return reserveAmount;
    }

    public void setReserveAmount(int reserveAmount){
        this.reserveAmount = reserveAmount;
    }
}
