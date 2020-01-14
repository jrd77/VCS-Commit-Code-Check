package com.atzuche.order.renterorder.vo.owner;


/**
 * 下单前费用计算获取车主券参数
 *
 * @author pengcheng.fu
 * @date 2019/12/25 16:11
 */
public class OwnerCouponReqVO {

    /**
     * 租客会员号
     */
    private Integer memNo;

    /**
     * 车辆注册号
     */
    private Integer carNo;

    /**
     * 车主券码
     */
    private String couponNo;


    public OwnerCouponReqVO() {
    }

    public OwnerCouponReqVO(Integer memNo, Integer carNo, String couponNo) {
        this.memNo = memNo;
        this.carNo = carNo;
        this.couponNo = couponNo;
    }


    public Integer getMemNo() {
        return memNo;
    }

    public void setMemNo(Integer memNo) {
        this.memNo = memNo;
    }

    public Integer getCarNo() {
        return carNo;
    }

    public void setCarNo(Integer carNo) {
        this.carNo = carNo;
    }

    public String getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }
}
