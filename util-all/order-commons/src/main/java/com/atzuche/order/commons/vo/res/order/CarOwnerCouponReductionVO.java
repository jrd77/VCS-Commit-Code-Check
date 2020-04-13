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
    private Integer discouponDeductibleAmt;

    @AutoDocProperty(value = "可用优惠券列表")
    private List<CarOwnerCouponDetailVO> availableCouponList;


    public CarOwnerCouponReductionVO() {
    }

    public CarOwnerCouponReductionVO(Integer discouponDeductibleAmt) {
        this.discouponDeductibleAmt = discouponDeductibleAmt;
    }

    public Integer getDiscouponDeductibleAmt() {
        return discouponDeductibleAmt;
    }

    public void setDiscouponDeductibleAmt(Integer discouponDeductibleAmt) {
        this.discouponDeductibleAmt = discouponDeductibleAmt;
    }

    public List<CarOwnerCouponDetailVO> getAvailableCouponList() {
        return availableCouponList;
    }

    public void setAvailableCouponList(List<CarOwnerCouponDetailVO> availableCouponList) {
        this.availableCouponList = availableCouponList;
    }
}
