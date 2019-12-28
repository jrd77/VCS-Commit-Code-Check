package com.atzuche.order.coreapi.entity.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ModifyFlagDTO {

	/**
	 * 是否修改不急免赔
	 */
	private boolean modifyAbatementFlag;
	/**
	 * 是否修改取车时间
	 */
	private boolean modifyRentTimeFlag;
	/**
	 * 是否修改还车时间
	 */
	private boolean modifyRevertTimeFlag;
	/**
	 * 是否修改取车地址
	 */
	private boolean modifyGetAddrFlag;
	/**
	 * 是否修改还车地址
	 */
	private boolean modifyReturnAddrFlag;
	/**
	 * 是否修改附加驾驶人
	 */
	private boolean modifyDriverFlag;
	/**
	 * 是否修改取车标志
	 */
	private boolean modifySrvGetFlag;
	/**
	 * 是否修改还车标志
	 */
	private boolean modifySrvReturnFlag;
	/**
	 * 是否修改使用凹凸币
	 */
	private boolean modifyUserCoinFlag;
	/**
	 * 是否修改车主券
	 */
	private boolean modifyCarOwnerCouponFlag;
	/**
	 * 是否修改取还车券
	 */
	private boolean modifyGetReturnCouponFlag;
	/**
	 * 是否修改平台券
	 */
	private boolean modifyPlatformCouponFlag;
}
