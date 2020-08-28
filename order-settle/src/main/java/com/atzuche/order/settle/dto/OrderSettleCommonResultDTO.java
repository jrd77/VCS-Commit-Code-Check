package com.atzuche.order.settle.dto;

/**
 * @author pengcheng.fu
 * @date 2020/8/19 17:07
 */
public class OrderSettleCommonResultDTO {

    /**
     * 退款状态
     */
    private Integer status;

    /**
     * 剩余金额
     */
    private Integer surplusAmt;

    public OrderSettleCommonResultDTO() {
    }

    public OrderSettleCommonResultDTO(Integer status) {
        this.status = status;
    }

    public OrderSettleCommonResultDTO(Integer status, Integer surplusAmt) {
        this.status = status;
        this.surplusAmt = surplusAmt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSurplusAmt() {
        return surplusAmt;
    }

    public void setSurplusAmt(Integer surplusAmt) {
        this.surplusAmt = surplusAmt;
    }

    @Override
    public String toString() {
        return "OrderSettleCommonResultDTO{" +
                "status=" + status +
                ", surplusAmt=" + surplusAmt +
                '}';
    }
}
