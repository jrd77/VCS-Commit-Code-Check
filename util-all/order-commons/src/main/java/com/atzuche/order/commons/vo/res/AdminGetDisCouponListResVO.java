package com.atzuche.order.commons.vo.res;


import com.atzuche.order.commons.vo.res.coupon.DisCoupon;
import com.atzuche.order.commons.vo.res.coupon.OwnerDisCoupon;
import com.autoyol.doc.annotation.AutoDocProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 获取订单内优惠券抵扣信息返回结果
 *
 * @author pengcheng.fu
 * @date 2020/2/13 12:24
 */
public class AdminGetDisCouponListResVO implements Serializable {

	private static final long serialVersionUID = 3608767924994655911L;

	@AutoDocProperty(value = "车主优惠券列表")
	private List<OwnerDisCoupon> ownerDisCouponList;

	@AutoDocProperty(value = "平台优惠券列表")
	private List<DisCoupon> platformCouponList;

	@AutoDocProperty(value = "送取服务券列表")
	private List<DisCoupon> getCarFeeDisCouponList;

	@AutoDocProperty(value = "租客钱包余额")
	private String walletBal;


	public List<OwnerDisCoupon> getOwnerDisCouponList() {
		return ownerDisCouponList;
	}

	public void setOwnerDisCouponList(List<OwnerDisCoupon> ownerDisCouponList) {
		this.ownerDisCouponList = ownerDisCouponList;
	}

	public List<DisCoupon> getPlatformCouponList() {
		return platformCouponList;
	}

	public void setPlatformCouponList(List<DisCoupon> platformCouponList) {
		this.platformCouponList = platformCouponList;
	}

	public List<DisCoupon> getGetCarFeeDisCouponList() {
		return getCarFeeDisCouponList;
	}

	public void setGetCarFeeDisCouponList(List<DisCoupon> getCarFeeDisCouponList) {
		this.getCarFeeDisCouponList = getCarFeeDisCouponList;
	}

	public String getWalletBal() {
		return walletBal;
	}

	public void setWalletBal(String walletBal) {
		this.walletBal = walletBal;
	}
}
