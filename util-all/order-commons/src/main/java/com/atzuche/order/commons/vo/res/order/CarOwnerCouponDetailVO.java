package com.atzuche.order.commons.vo.res.order;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.io.Serializable;

/**
 * 车主优惠券信息
 *
 * @author pengcheng.fu
 * @date 2020/1/11 15:16
 */
public class CarOwnerCouponDetailVO implements Serializable {

    private static final long serialVersionUID = 505747564686078128L;
    @AutoDocProperty(value = "优惠券码")
    private String couponNo;
    @AutoDocProperty(value = "条件金额")
    private Integer condAmount;
    @AutoDocProperty(value = "抵扣金额")
    private Integer discount;
    @AutoDocProperty(value = "有效开始时间(yyyy.MM.dd HH:mm)")
    private String validBeginTime;
    @AutoDocProperty(value = "有效结束时间(yyyy.MM.dd HH:mm)")
    private String validEndTime;
    @AutoDocProperty(value = "优惠券类型 例如:车主优惠券")
    private String couponName;
    @AutoDocProperty(value = "优惠券适用条件 例如: 满500可用")
    private String abstractTxt;
    @AutoDocProperty(value = "优惠券适用场景 例如: 仅抵扣租金")
    private String detail;
    @AutoDocProperty(value = "是否选中")
    private Boolean select;

    public String getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }

    public Integer getCondAmount() {
        return condAmount;
    }

    public void setCondAmount(Integer condAmount) {
        this.condAmount = condAmount;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getValidBeginTime() {
        return validBeginTime;
    }

    public void setValidBeginTime(String validBeginTime) {
        this.validBeginTime = validBeginTime;
    }

    public String getValidEndTime() {
        return validEndTime;
    }

    public void setValidEndTime(String validEndTime) {
        this.validEndTime = validEndTime;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getAbstractTxt() {
        return abstractTxt;
    }

    public void setAbstractTxt(String abstractTxt) {
        this.abstractTxt = abstractTxt;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Boolean getSelect() {
        return select;
    }

    public void setSelect(Boolean select) {
        this.select = select;
    }
}
