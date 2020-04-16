package com.atzuche.order.commons.vo.res.order;

import java.io.Serializable;
import java.util.List;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * 优惠券抵扣信息
 *
 * @author pengcheng.fu
 * @date 2020/1/11 15:16
 */
public class CouponReductionVO implements Serializable {

    private static final long serialVersionUID = 8077806392449887573L;

    @AutoDocProperty(value = "优惠券抵扣金额，为负数“如-30”")
    private Integer discouponDeductibleAmt;

    @AutoDocProperty(value = "优惠券列表")
    private List<DisCouponMemInfoVO> discoupons;


    public Integer getDiscouponDeductibleAmt() {
        return discouponDeductibleAmt;
    }

    public void setDiscouponDeductibleAmt(Integer discouponDeductibleAmt) {
        this.discouponDeductibleAmt = discouponDeductibleAmt;
    }

    public List<DisCouponMemInfoVO> getDiscoupons() {
        return discoupons;
    }

    public void setDiscoupons(List<DisCouponMemInfoVO> discoupons) {
        this.discoupons = discoupons;
    }
}