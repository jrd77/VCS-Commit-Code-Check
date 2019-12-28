package com.atzuche.order.coreapi.entity.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.ToString;
@Data
@ToString
public class ModifyOrderDTO {
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 租客子订单号
	 */
	private String renterOrderNo;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 补充全险是否开启，0：否，1：是
	 */
	private Integer abatementFlag;
	/**
	 * 取车时间
	 */
	private LocalDateTime rentTime;
	/**
	 * 还车时间
	 */
	private LocalDateTime revertTime;
	/**
	 * 取车地址
	 */
	private String getCarAddress;
	/**
	 * 取车地址纬度
	 */
	private String getCarLat;
	/**
	 * 取车地址经度
	 */
	private String getCarLon;
	/**
	 * 还车地址
	 */
	private String revertCarAddress;
	/**
	 * 还车地址纬度
	 */
	private String revertCarLat;
	/**
	 * 还车地址经度
	 */
	private String revertCarLon;
	/**
	 * 附加驾驶员ID列表
	 */
	private List<String> driverIds;
	
	/**
	 * 是否是管理后台操作 true是，其他否
	 */
	private Boolean consoleFlag;
	/**
	 * 使用取车服务标志 0-不使用，1-使用
	 */
	private Integer srvGetFlag;
	/**
	 * 使用还车服务标志0-不使用，1-使用
	 */
	private Integer srvReturnFlag;
	/**
	 * 是否使用凹凸币0-否，1-是
	 */
	private Integer userCoinFlag;
	/**
	 * 车主券id
	 */
	private String carOwnerCouponId;
	/**
	 * 取还车优惠券id
	 */
	private String srvGetReturnCouponId;
	/**
	 * 平台优惠券id
	 */
	private String platformCouponId;
	/**
	 * 修改哪种操作标记
	 */
	private ModifyFlagDTO modifyFlagDTO;
}
