package com.atzuche.order.settle.dto;

import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;

/**
 * @author pengcheng.fu
 * @date 2020/8/19 17:04
 */
public class OrderSettleCommonParamDTO {

    /**
    * 主订单号
     */
    private String orderNo;
    /**
     * 租客订单号
     */
    private String renterOrderNo;

    /**
     * 租客会员号
     */
    private String memNo;

    /**
     * 费用项
     */
    private RenterCashCodeEnum cashCodeEnum;

    /**
     * 租车费用编码(结算和取消结算不统一特殊处理)
     */
    private RenterCashCodeEnum rentCarCostCashCodeEnum;


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getRenterOrderNo() {
        return renterOrderNo;
    }

    public void setRenterOrderNo(String renterOrderNo) {
        this.renterOrderNo = renterOrderNo;
    }

    public String getMemNo() {
        return memNo;
    }

    public void setMemNo(String memNo) {
        this.memNo = memNo;
    }

    public RenterCashCodeEnum getCashCodeEnum() {
        return cashCodeEnum;
    }

    public void setCashCodeEnum(RenterCashCodeEnum cashCodeEnum) {
        this.cashCodeEnum = cashCodeEnum;
    }

    public RenterCashCodeEnum getRentCarCostCashCodeEnum() {
        return rentCarCostCashCodeEnum;
    }

    public void setRentCarCostCashCodeEnum(RenterCashCodeEnum rentCarCostCashCodeEnum) {
        this.rentCarCostCashCodeEnum = rentCarCostCashCodeEnum;
    }


    @Override
    public String toString() {
        return "OrderSettleCommonParamDTO{" +
                "orderNo='" + orderNo + '\'' +
                ", renterOrderNo='" + renterOrderNo + '\'' +
                ", memNo='" + memNo + '\'' +
                ", cashCodeEnum=" + cashCodeEnum +
                ", rentCarCostCashCodeEnum=" + rentCarCostCashCodeEnum +
                '}';
    }



}
