package com.atzuche.order.parentorder.dto;

/**
 * SuccessOrderDTO
 *
 * @author shisong
 * @date 2020/1/4
 */
public class SuccessOrderDTO {

    private String orderNo;
    private String renterNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getRenterNo() {
        return renterNo;
    }

    public void setRenterNo(String renterNo) {
        this.renterNo = renterNo;
    }

    @Override
    public String toString() {
        return "SuccessOrderDTO{" +
                "orderNo='" + orderNo + '\'' +
                ", renterNo='" + renterNo + '\'' +
                '}';
    }
}
