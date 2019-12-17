package com.atzuche.order.parentorder.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 主订单表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:52:41
 * @Description:
 */
@Data
public class OrderEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 在途车主子订单号
	 */
	private String ownerOrderNo;
	/**
	 * 在途租客子订单号
	 */
	private String renterOrderNo;
	/**
	 * 在途车主会员号
	 */
	private Integer memNoOwner;
	/**
	 * 在途租客会员号
	 */
	private Integer memNoRenter;
	/**
	 * 配送订单号
	 */
	private String orderNoDelivery;
	/**
	 * 订单类型（内部分类）1：短租， 2：套餐
	 */
	private Integer category;
	/**
	 * 场景号
	 */
	private String entryCode;
	/**
	 * 预计起租时间
	 */
	private LocalDateTime expRentStartTime;
	/**
	 * 预计还车时间
	 */
	private LocalDateTime expRentEndTime;
	/**
	 * 实际起租时间
	 */
	private LocalDateTime actRentStartTime;
	/**
	 * 实际还车时间
	 */
	private LocalDateTime actRentEndTime;
	/**
	 * 城市名称
	 */
	private String cityName;
	/**
	 * 城市code
	 */
	private String cityCode;
	/**
	 * 是否出市
	 */
	private Integer isOutCity;
	/**
	 * 是否使用凹凸币
	 */
	private Integer isUseCoin;
	/**
	 * 是否使用优惠券
	 */
	private Integer isUseCoupon;
	/**
	 * 是否使用钱包
	 */
	private Integer isUseWallet;
	/**
	 * 是否理赔
	 */
	private Integer isClaims;
	/**
	 * 是否免押
	 */
	private Integer isFreeDeposit;
	/**
	 * 是否违章
	 */
	private Integer isWz;
	/**
	 * 是否使用机场服务
	 */
	private Integer isUseAirPortService;
	/**
	 * 是否暂扣
	 */
	private Integer isDetain;
	/**
	 * 请求时间
	 */
	private LocalDateTime reqTime;
	/**
	 * 风控审核id
	 */
	private Integer riskAuditId;
	/**
	 * 版本号
	 */
	private Integer version;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 创建人
	 */
	private String createOp;
	/**
	 * 修改时间
	 */
	private LocalDateTime updateTime;
	/**
	 * 修改人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
