package com.atzuche.order.parentorder.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 
 * 
 * @author ZhangBin
 * @date 2020-03-11 15:34:01
 * @Description:
 */
@Data
public class OrderNoticeEntity implements Serializable {
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
	 * 车主会员号
	 */
	private String ownerMem;
	/**
	 * 标记是否关闭 1：是  0：否
	 */
	private Integer closeFlag;
	/**
	 * 标记方：1：租客端，2：车主端
	 */
	private Integer sourceCode;
	/**
	 * 
	 */
	private LocalDateTime createTime;
	/**
	 * 
	 */
	private String createOp;
	/**
	 * 
	 */
	private LocalDateTime updateTime;
	/**
	 * 
	 */
	private String updateOp;
	/**
	 * 
	 */
	private Integer isDelete;

}
