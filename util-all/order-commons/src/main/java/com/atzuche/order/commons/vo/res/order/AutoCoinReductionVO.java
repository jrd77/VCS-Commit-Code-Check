package com.atzuche.order.commons.vo.res.order;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.io.Serializable;

/**
 * 凹凸币抵扣信息
 *
 * @author pengcheng.fu
 * @date 2020/1/11 15:16
 */
public class AutoCoinReductionVO implements Serializable {

    private static final long serialVersionUID = -6083768702820984984L;

    @AutoDocProperty(value = "是否显示凹凸币(0:不显示,1:显示)")
    private String autoCoinIsShow;

    @AutoDocProperty(value = "凹凸币抵扣金额，如-8")
    private Integer autoCoinDeductibleAmt;

    @AutoDocProperty(value = "凹凸币余额(包含当前抵扣部分)，如-8")
    private Integer availableAutoCoinAmt;


    public String getAutoCoinIsShow() {
        return autoCoinIsShow;
    }

    public void setAutoCoinIsShow(String autoCoinIsShow) {
        this.autoCoinIsShow = autoCoinIsShow;
    }

    public Integer getAutoCoinDeductibleAmt() {
        return autoCoinDeductibleAmt;
    }

    public void setAutoCoinDeductibleAmt(Integer autoCoinDeductibleAmt) {
        this.autoCoinDeductibleAmt = autoCoinDeductibleAmt;
    }

    public Integer getAvailableAutoCoinAmt() {
        return availableAutoCoinAmt;
    }

    public void setAvailableAutoCoinAmt(Integer availableAutoCoinAmt) {
        this.availableAutoCoinAmt = availableAutoCoinAmt;
    }
}
