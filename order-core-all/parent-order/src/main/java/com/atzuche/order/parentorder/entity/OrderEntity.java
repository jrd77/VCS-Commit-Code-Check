package com.atzuche.order.parentorder.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 主订单表
 * 
 * @author ZhangBin
 * @date 2019-12-28 15:37:56
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
	 * 租客会员号
	 */
	private String memNoRenter;
	/**
	 * 订单类型（内部分类）1：短租， 2：套餐
	 */
	private Integer category;
	/**
	 * 场景号
	 */
	private String entryCode;
	/**
	 * 来源 1：手机，2：网站，3:管理后台，4:cp b2c, 5:cp upop
	 */
	private String source;
	/**
	 * 预计起租时间
	 */
	private LocalDateTime expRentTime;
	/**
	 * 预计还车时间
	 */
	private LocalDateTime expRevertTime;
	/**
	 * 下单城市名称
	 */
	private String cityName;
	/**
	 * 下单城市code
	 */
	private String cityCode;
	/**
	 * 是否出市 0-否，1-是
	 */
	private Integer isOutCity;
    /**
     * 租车城市（用车城市）
     */
	private String rentCity;
	/**
	 * 是否免押 0-否，1-是
	 */
	private Integer isFreeDeposit;
	/**
	 * 是否使用机场服务 0-否，1-是
	 */
	private Integer isUseAirPortService;
	/**
	 * 航班号
	 */
	private String flightId;
	/**
	 * 请求时间
	 */
	private LocalDateTime reqTime;
	/**
	 * 限时立减金额（面额）
	 */
	private Integer limitAmt;
	/**
	 * 风控审核id
	 */
	private String riskAuditId;
	/**
	 * 版本号
	 */
	private Integer version;
	/**
	 * 订单图片存储目录
	 */
	private String basePath;
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
