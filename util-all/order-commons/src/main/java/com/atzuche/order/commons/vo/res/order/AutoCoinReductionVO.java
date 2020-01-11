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
    private String autoCoinDeductibleAmt;
    @AutoDocProperty(value = "凹凸币抵扣金额描述，如:(800个)")
    private String autoCoinDeductibleAmtText;
    @AutoDocProperty(value = "使用凹凸币描述,如:使用凹凸币抵扣(800个，可抵8元)")
    private String autoCoinUseText;
    @AutoDocProperty(value = "凹凸币可用描述,如:（有100000个，可抵扣1000元）,如果没有可抵扣的凹凸币,100为一个抵扣单位，无需返回")
    private String autoCoinText;
    @AutoDocProperty(value = "凹凸币低于100个大于0个文案描述,如:（有100000个）")
    private String autoCoinLowLimitText;
    @AutoDocProperty(value = "【5.8新增】凹凸币使用规则小i跳转url")
    private String autoCoinRuleUrl;

    public String getAutoCoinIsShow() {
        return autoCoinIsShow;
    }

    public void setAutoCoinIsShow(String autoCoinIsShow) {
        this.autoCoinIsShow = autoCoinIsShow;
    }

    public String getAutoCoinDeductibleAmt() {
        return autoCoinDeductibleAmt;
    }

    public String getAutoCoinDeductibleAmtText() {
        return autoCoinDeductibleAmtText;
    }

    public String getAutoCoinUseText() {
        return autoCoinUseText;
    }

    public String getAutoCoinText() {
        return autoCoinText;
    }

    public String getAutoCoinLowLimitText() {
        return autoCoinLowLimitText;
    }

    public void setAutoCoinDeductibleAmt(String autoCoinDeductibleAmt) {
        this.autoCoinDeductibleAmt = autoCoinDeductibleAmt;
    }

    public void setAutoCoinDeductibleAmtText(String autoCoinDeductibleAmtText) {
        this.autoCoinDeductibleAmtText = autoCoinDeductibleAmtText;
    }

    public void setAutoCoinUseText(String autoCoinUseText) {
        this.autoCoinUseText = autoCoinUseText;
    }

    public void setAutoCoinText(String autoCoinText) {
        this.autoCoinText = autoCoinText;
    }

    public void setAutoCoinLowLimitText(String autoCoinLowLimitText) {
        this.autoCoinLowLimitText = autoCoinLowLimitText;
    }

    public String getAutoCoinRuleUrl() {
        return autoCoinRuleUrl;
    }

    public void setAutoCoinRuleUrl(String autoCoinRuleUrl) {
        this.autoCoinRuleUrl = autoCoinRuleUrl;
    }
}
