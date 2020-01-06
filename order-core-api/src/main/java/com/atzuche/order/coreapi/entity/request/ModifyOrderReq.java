package com.atzuche.order.coreapi.entity.request;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ModifyOrderReq {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
	@NotBlank(message="memNo不能为空")
	@AutoDocProperty(value="memNo,必填，",required=true)
	private String memNo;
	
	@AutoDocProperty(value="补充全险是否开启，0：否，1：是")
	private Integer abatementFlag;
	
	@AutoDocProperty(value="取车时间,格式 yyyy-MM-dd HH:mm:ss",required=true)
	private String rentTime;
	
	@AutoDocProperty(value="还车时间,格式 yyyy-MM-dd HH:mm:ss",required=true)
	private String revertTime;
	
	@AutoDocProperty(value="取车地址")
	private String getCarAddress;
	
	@AutoDocProperty(value="取车地址纬度")
    private String getCarLat;
	
	@AutoDocProperty(value="取车地址经度")
    private String getCarLon;
	
	@AutoDocProperty(value="还车地址")
	private String revertCarAddress;
	
	@AutoDocProperty(value="还车地址纬度")
    private String revertCarLat;
	
	@AutoDocProperty(value="还车地址经度")
    private String revertCarLon;
	
	@AutoDocProperty(value="【增加附加驾驶员】附加驾驶员ID列表")
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
	 * 租客费用补贴
	 */
    @AutoDocProperty(value="租客费用补贴", hidden = true)
	private List<RenterOrderSubsidyDetailDTO> renterSubsidyList;
}
