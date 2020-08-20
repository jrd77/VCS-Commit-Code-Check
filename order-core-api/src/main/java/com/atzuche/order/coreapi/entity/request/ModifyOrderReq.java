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
	
	@NotBlank(message="租客memNo不能为空")
	@AutoDocProperty(value="租客memNo,必填，",required=true)
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
	private Boolean consoleFlag;
	/**
	 * 管理后台修改原因
	 */
	private String modifyReason;
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
     * 是否使用特供价（换车用）1-使用，0-不使用
     */
    private Integer useSpecialPriceFlag;
    /**
     * 车辆注册号（换车用）
     */
    private String carNo;
    /**
     * 是否换车操作 （换车用）
     */
    private Boolean transferFlag;
    /**
     * 管理后台操作人
     */
    private String operator;
	/**
	 * 租客费用补贴
	 */
    @AutoDocProperty(value="租客费用补贴", hidden = true)
	private List<RenterOrderSubsidyDetailDTO> renterSubsidyList;
    
    /**
     * 是否扫码还车的修改还车时间
     */
    private Boolean scanCodeFlag;
    
    @AutoDocProperty(value="是否购买轮胎保障服务 0-不购买，1-购买")
	private Integer tyreInsurFlag;
	@AutoDocProperty(value="是否购买驾乘无忧保障服务 0-不购买，1-购买")
	private Integer driverInsurFlag;
	@AutoDocProperty(value = "配送模式：0-区间配送，1-精准配送")
    private Integer distributionMode;
	@AutoDocProperty(value="是否超级权限，可以跳过校验：0-否，1-是")
	private Integer superPowerFlag;
	
	@AutoDocProperty(value="管理后台是否使用钱包：0-否，1-是")
	private Integer useWalletFlag;
}
