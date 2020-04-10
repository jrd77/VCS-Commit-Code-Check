package com.atzuche.order.coreapi.entity.dto;

import com.atzuche.order.commons.entity.dto.CarRentTimeRangeDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.dto.OrderChangeItemDTO;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
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
	 * 修改哪种操作
	 */
	private List<OrderChangeItemDTO> changeItemList;
	/**
     * 提前时间（分钟数）
     */
    private Integer getCarBeforeTime;
    /**
     * 延后时间（分钟数）
     */
    private Integer returnCarAfterTime;
    /**
     * 城市code
     */
    private String cityCode;
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
     * 管理后台修改原因
     */
    private String changeReason;
    /**
	 * 租客费用补贴
	 */
	private List<RenterOrderSubsidyDetailDTO> renterSubsidyList;
	/**
	 * 租客商品信息
	 */
	private RenterGoodsDetailDTO renterGoodsDetailDTO;
	/**
	 * 租客会员信息
	 */
	private RenterMemberDTO renterMemberDTO;
	/**
	 * 主订单信息
	 */
	private OrderEntity orderEntity;
	/**
	 * 已使用的券对象列表
	 */
	private List<OrderCouponEntity> orderCouponList;
	/**
	 * 订单状态
	 */
	private OrderStatusEntity orderStatusEntity;
	/**
	 * 提前延后时间
	 */
	private CarRentTimeRangeDTO carRentTimeRangeResVO;
	/**
     * 是否扫码还车的修改还车时间
     */
    private Boolean scanCodeFlag;
    /**
     * 长租优惠券码
     */
    private String longCouponCode;
}
