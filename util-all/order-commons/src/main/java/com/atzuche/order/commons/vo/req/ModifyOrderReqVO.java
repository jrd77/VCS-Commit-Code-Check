package com.atzuche.order.commons.vo.req;

import com.atzuche.order.commons.entity.dto.RenterOrderSubsidyDetailDTO;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

@Data
@ToString
public class ModifyOrderReqVO {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
	//@NotBlank(message="memNo不能为空")
	@AutoDocProperty(value="memNo,必填，")
	private String memNo;
	
	@AutoDocProperty(value="补充全险是否开启，0：否，1：是")
	private Integer abatementFlag;
	
	@AutoDocProperty(value="取车时间,格式 yyyyMMddHHmmss",required=true)
	private String rentTime;
	
	@AutoDocProperty(value="还车时间,格式 yyyyMMddHHmmss",required=true)
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
	@AutoDocProperty("是否是管理后台操作 true是，其他否")
	private Boolean consoleFlag;
	/**
	 * 使用取车服务标志 0-不使用，1-使用
	 */
	@AutoDocProperty("使用取车服务标志 0-不使用，1-使用")
	private Integer srvGetFlag;
	/**
	 * 使用还车服务标志0-不使用，1-使用
	 */
	@AutoDocProperty("使用还车服务标志0")
	private Integer srvReturnFlag;
	/**
	 * 是否使用凹凸币0-否，1-是
	 */
	@AutoDocProperty("是否使用凹凸币0-否，1-是")
	private Integer userCoinFlag;
	/**
	 * 车主券id
	 */
	@AutoDocProperty("车主券id")
	private String carOwnerCouponId;
	/**
	 * 取还车优惠券id
	 */
	@AutoDocProperty("取还车优惠券id")
	private String srvGetReturnCouponId;
	/**
	 * 平台优惠券id
	 */
	@AutoDocProperty("平台优惠券id")
	private String platformCouponId;
	/**
	 * 租客费用补贴
	 */
    @AutoDocProperty(value="租客费用补贴", hidden = true)
	private List<RenterOrderSubsidyDetailDTO> renterSubsidyList;

    @AutoDocProperty("修改原因")
    private String modifyReason;
    /**
     * 管理后台操作人
     */
    private String operator;
    /**
	 * 是否购买轮胎保障服务
	 */
	private Integer tyreInsurFlag;
	/**
	 * 是否购买驾乘无忧保障服务
	 */
	private Integer driverInsurFlag;
	@AutoDocProperty(value="是否超级权限，可以跳过校验：0-否，1-是")
	private Integer superPowerFlag;
}
