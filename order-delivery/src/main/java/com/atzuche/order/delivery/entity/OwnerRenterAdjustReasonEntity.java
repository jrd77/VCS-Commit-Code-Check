package com.atzuche.order.delivery.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 车主和租客相互调价原因和备注表
 * 
 * @author ZhangBin
 * @date 2020-07-29 13:34:29
 * @Description:
 */
@Data
public class OwnerRenterAdjustReasonEntity implements Serializable {
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
	 * 车主子订单号
	 */
	private String ownerOrderNo;
	/**
	 * 租客子订单号
	 */
	private String renterOrderNo;
	/**
	 * 租客给车主调价：1，车主给租客调价：2
	 */
	private Integer adjustTarget;
	/**
	 * 调价原因类型
	 */
	private Integer adjustReasonType;
	/**
	 * 调价原因描述
	 */
	private String adjustReasonDesc;
	/**
	 * 当前调价原因的备注
	 */
	private String adjustRemark;
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
