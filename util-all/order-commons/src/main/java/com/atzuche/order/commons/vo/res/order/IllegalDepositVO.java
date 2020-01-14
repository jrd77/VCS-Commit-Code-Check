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

    @AutoDocProperty(value = "违章押金")
    private Integer illegalDepositAmt;

    public Integer getIllegalDepositAmt() {
        return illegalDepositAmt;
    }

    public void setIllegalDepositAmt(Integer illegalDepositAmt) {
        this.illegalDepositAmt = illegalDepositAmt;
    }

}
