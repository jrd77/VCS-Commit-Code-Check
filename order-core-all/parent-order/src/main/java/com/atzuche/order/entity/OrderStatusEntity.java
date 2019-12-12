package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 主订单表状态
 * 
 * @author ZhangBin
 * @date 2019-12-12 14:41:07
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
	private Integer orderNo;
	/**
	 * 租车费用支付状态（待支付、已支付）
	 */
	private Integer rentCarPayStatus;
	/**
	 * 租车费用退款状态（待退款、已退款）
	 */
	private Integer rentCarRefundStatus;
	/**
	 * 车辆押金支付状态
	 */
	private Integer depositPayStatus;
	/**
	 * 车辆押金退款状态
	 */
	private Integer depositRefundStatus;
	/**
	 * 违章押金支付状态
	 */
	private Integer wzPayStatus;
	/**
	 * 违章押金退款状态
	 */
	private Integer wzRefundStatus;
	/**
	 * 费用结算状态
	 */
	private Integer settleStatus;
	/**
	 * 主状态: 待确认,修改待确认,待调度,待支付,待交车,待还车,待结算,待违章处理,待违章结算,已完结,已结束,待理赔处理
	 */
	private Integer status;
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
