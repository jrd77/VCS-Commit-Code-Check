package com.atzuche.order.parentorder.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 主订单表状态
 * 
 * @author ZhangBin
 * @date 2020-01-03 12:01:48
 * @Description:
 */
@Data
public class OrderStatusEntity implements Serializable {
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
	 * 租车费用支付状态:0,待支付 1,已支付
	 */
	private Integer rentCarPayStatus;
	/**
	 * 租车费用退款状态:0,待退款 1,已退款
	 */
	private Integer rentCarRefundStatus;
	/**
	 * 车辆押金支付状态:0,待支付 1,已支付
	 */
	private Integer depositPayStatus;
	/**
	 * 车辆押金退款状态:0,待退款 1,已退款
	 */
	private Integer depositRefundStatus;
	/**
	 * 违章押金支付状态:0,待支付 1,已支付
	 */
	private Integer wzPayStatus;
	/**
	 * 违章押金退款状态:0,待退款 1,已退款
	 */
	private Integer wzRefundStatus;
	/**
	 * 违章结算状态:0,否 1,是
	 */
	private Integer wzSettleStatus;
	/**
	 * 违章结算时间
	 */
	private LocalDateTime wzSettleTime;
	/**
	 * 车辆押金结算状态:0,否 1,是
	 */
	private Integer carDepositSettleStatus;
	/**
	 * 车辆押金结算时间
	 */
	private LocalDateTime carDepositSettleTime;
	/**
	 * 租车费用结算状态:0,否 1,是
	 */
	private Integer settleStatus;
	/**
	 * 租车费用结算时间
	 */
	private LocalDateTime settleTime;
	/**
	 * 是否支持调度:0,否 1,是(调度未结果) 2,是(调度有结果)
	 */
	private Integer isDispatch;
	/**
	 * 调度状态:0,未调度 1,调度中 2,调度成功 3,调度失败
	 */
	private Integer dispatchStatus;
	/**
	 * 是否理赔 0-否，1-是
	 */
	private Integer isClaims;
	/**
	 * 是否暂扣  0-否，1-是
	 */
	private Integer isDetain;
	/**
	 * 是否违章 0-否，1-是
	 */
	private Integer isWz;
	/**
	 * 是否有风控事故 0-否 1-是
	 */
	private Integer isRiskAccident;
	/**
	 * 主订单状态: 0,待确认 1,待调度 2,待支付 3,待交车 4,待还车 5,待结算 6,待违章处理 7,待违章结算 8,已完结 9,待理赔处理 20,已结束
	 */
	private Integer status;
	/**
	 * 取还车节点状态：取还车节点状态：1、已安排取送车人员，2、车管家送车中，3、车辆已送达，4、已安排取还车人员，5、从租客处取车成功，6、给车主还车成功。
	 */
	private Integer nodeState;
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
