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


    public Integer getDepositAmt() {
        return depositAmt;
    }

    public void setDepositAmt(Integer depositAmt) {
        this.depositAmt = depositAmt;
    }

}
