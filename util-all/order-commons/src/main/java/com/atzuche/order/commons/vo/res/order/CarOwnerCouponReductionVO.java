package com.atzuche.order.commons.vo.res.order;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 车主优惠券抵扣信息
 *
 * @author pengcheng.fu
 * @date 2020/1/11 15:16
 */
public class CarOwnerCouponReductionVO implements Serializable {

    private static final long serialVersionUID = 2556052833923486798L;

    @AutoDocProperty(value = "优惠券抵扣金额，为负数“如-30”")
    private String discouponDeductibleAmt;
    @AutoDocProperty(value = "被选中的优惠券号")
    private String couponNo;
    @AutoDocProperty(value = "可用优惠券列表")
    private List<CarOwnerCouponDetailVO> availableCouponList;
    @AutoDocProperty(value = "未使用优惠券时，优惠券可用数描述，如:（有30张可用）,如果没有可用优惠券，无需返回")
    private String discouponText;

    public String getDiscouponDeductibleAmt() {
        return discouponDeductibleAmt;
    }

    public void setDiscouponDeductibleAmt(String discouponDeductibleAmt) {
        this.discouponDeductibleAmt = discouponDeductibleAmt;
    }

    public String getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }

    public List<CarOwnerCouponDetailVO> getAvailableCouponList() {
        return availableCouponList;
    }

    public void setAvailableCouponList(
            List<CarOwnerCouponDetailVO> availableCouponList) {
        this.availableCouponList = availableCouponList;
    }

    public String getDiscouponText() {
        return discouponText;
    }

    public void setDiscouponText(String discouponText) {
        this.discouponText = discouponText;
    }
}
