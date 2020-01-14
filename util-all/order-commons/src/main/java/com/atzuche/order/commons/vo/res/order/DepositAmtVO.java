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

    @AutoDocProperty(value = "车辆押金")
    private Integer depositAmt;

    @AutoDocProperty(value = "车辆押金减免金额")
    private Integer reductionAmt;

    @AutoDocProperty(value = "车辆押金减免比例")
    private Integer reductionRate;



    public Integer getDepositAmt() {
        return depositAmt;
    }

    public void setDepositAmt(Integer depositAmt) {
        this.depositAmt = depositAmt;
    }

    public Integer getReductionAmt() {
        return reductionAmt;
    }

    public void setReductionAmt(Integer reductionAmt) {
        this.reductionAmt = reductionAmt;
    }

    public Integer getReductionRate() {
        return reductionRate;
    }

    public void setReductionRate(Integer reductionRate) {
        this.reductionRate = reductionRate;
    }
}
