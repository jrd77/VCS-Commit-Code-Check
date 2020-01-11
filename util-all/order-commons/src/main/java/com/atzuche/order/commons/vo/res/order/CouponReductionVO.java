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
    private String discouponDeductibleAmt;

    @AutoDocProperty(value = "被选中的优惠券")
    private String discouponId;

    @AutoDocProperty(value = "被选中的取还车优惠券")
    private String getCarFreeCouponId;

    @AutoDocProperty(value = "优惠券列表")
    private List<DisCouponMemInfoVO> discoupons;

    @AutoDocProperty(value = "未使用优惠券时，优惠券可用数描述，如:（有30张可用）,如果没有可用优惠券，无需返回")
    private String discouponText;

    @AutoDocProperty(value = "是否有优惠券(包过本次不能用的)，0：没有，1：有")
    private String isHaveDiscoupons;


    public String getGetCarFreeCouponId() {
        return getCarFreeCouponId;
    }

    public void setGetCarFreeCouponId(String getCarFreeCouponId) {
        this.getCarFreeCouponId = getCarFreeCouponId;
    }

    public String getDiscouponDeductibleAmt() {
        return discouponDeductibleAmt;
    }

    public String getDiscouponId() {
        return discouponId;
    }

    public List<DisCouponMemInfoVO> getDiscoupons() {
        return discoupons;
    }

    public String getDiscouponText() {
        return discouponText;
    }

    public String getIsHaveDiscoupons() {
        return isHaveDiscoupons;
    }

    public void setDiscouponDeductibleAmt(String discouponDeductibleAmt) {
        this.discouponDeductibleAmt = discouponDeductibleAmt;
    }

    public void setDiscouponId(String discouponId) {
        this.discouponId = discouponId;
    }

    public void setDiscoupons(List<DisCouponMemInfoVO> discoupons) {
        this.discoupons = discoupons;
    }

    public void setDiscouponText(String discouponText) {
        this.discouponText = discouponText;
    }

    public void setIsHaveDiscoupons(String isHaveDiscoupons) {
        this.isHaveDiscoupons = isHaveDiscoupons;
    }
}
