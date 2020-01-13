package com.atzuche.order.commons.vo.res.order;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.io.Serializable;

/**
 * 抵扣信息
 *
 * @author pengcheng.fu
 * @date 2020/1/11 15:16
 */
public class ReductionVO implements Serializable {

    private static final long serialVersionUID = 2876709162980179823L;

    @AutoDocProperty(value = "优惠券抵扣信息")
    private CouponReductionVO couponReduction;

    @AutoDocProperty(value = "钱包余额抵扣信息")
    private WalletReductionVO walletReduction;

    @AutoDocProperty(value = "凹凸币抵扣信息")
    private AutoCoinReductionVO autoCoinReduction;

    @AutoDocProperty(value = "车主券抵扣信息")
    private CarOwnerCouponReductionVO carOwnerCouponReduction;


    public CouponReductionVO getCouponReduction() {
        return couponReduction;
    }

    public void setCouponReduction(CouponReductionVO couponReduction) {
        this.couponReduction = couponReduction;
    }

    public WalletReductionVO getWalletReduction() {
        return walletReduction;
    }

    public void setWalletReduction(WalletReductionVO walletReduction) {
        this.walletReduction = walletReduction;
    }

    public AutoCoinReductionVO getAutoCoinReduction() {
        return autoCoinReduction;
    }

    public void setAutoCoinReduction(AutoCoinReductionVO autoCoinReduction) {
        this.autoCoinReduction = autoCoinReduction;
    }

    public CarOwnerCouponReductionVO getCarOwnerCouponReduction() {
        return carOwnerCouponReduction;
    }

    public void setCarOwnerCouponReduction(CarOwnerCouponReductionVO carOwnerCouponReduction) {
        this.carOwnerCouponReduction = carOwnerCouponReduction;
    }
}
