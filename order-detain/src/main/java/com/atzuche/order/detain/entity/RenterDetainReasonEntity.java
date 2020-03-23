package com.atzuche.order.detain.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 租车押金暂扣原因表
 * 
 * @author ZhangBin
 * @date 2020-03-23 15:20:17
 */
@Data
public class RenterDetainReasonEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键
	 */
	private Integer id;
	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 暂扣类型编码
	 */
	private String detainTypeCode;
	/**
	 * 暂扣类型名称
	 */
	private String detainTypeName;
	/**
	 * 暂扣原因编码
	 */
	private String detainReasonCode;
	/**
	 * 暂扣原因描述
	 */
	private String detainReasonName;
	/**
	 * 是否暂扣:0,否 1,是
	 */
	private Integer detainStatus;
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
