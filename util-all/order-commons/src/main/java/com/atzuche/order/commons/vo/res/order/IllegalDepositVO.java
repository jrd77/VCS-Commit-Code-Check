package com.atzuche.order.commons.vo.res.order;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.io.Serializable;

/**
 * 违章押金信息
 *
 * @author pengcheng.fu
 * @date 2020/1/11 15:16
 */
public class IllegalDepositVO implements Serializable {

    private static final long serialVersionUID = 5262006441699646228L;

    @AutoDocProperty(value = "违章押金,如:2100")
    private String illegalDepositAmt;

    @AutoDocProperty(value = "预计退款时间,如:2017.11.13")
    private String refundTime;

    public String getIllegalDepositAmt() {
        return illegalDepositAmt;
    }

    public void setIllegalDepositAmt(String illegalDepositAmt) {
        this.illegalDepositAmt = illegalDepositAmt;
    }

    public String getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(String refundTime) {
        this.refundTime = refundTime;
    }
}
