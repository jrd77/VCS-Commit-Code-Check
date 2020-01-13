package com.atzuche.order.commons.vo.res.order;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.io.Serializable;

/**
 * 车辆押金信息
 *
 * @author pengcheng.fu
 * @date 2020/1/11 15:16
 */
public class DepositAmtVO implements Serializable {

    private static final long serialVersionUID = -3511873737686632869L;

    @AutoDocProperty(value = "车辆押金,如:2100")
    private String depositAmt;

    @AutoDocProperty(value = "预计退款时间,如:2017.11.13")
    private String refundTime;


    public String getDepositAmt() {
        return depositAmt;
    }

    public void setDepositAmt(String depositAmt) {
        this.depositAmt = depositAmt;
    }

    public String getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(String refundTime) {
        this.refundTime = refundTime;
    }
}
