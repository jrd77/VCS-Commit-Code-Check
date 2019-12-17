package com.atzuche.order.rentermem.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租客端会员权益表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:27:28
 * @Description:
 */
@Data
public class RenterMemberRightEntity implements Serializable {
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
	 * 子订单号
	 */
	private String renterOrderNo;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 权益编码
	 */
	private String rightCode;
	/**
	 * 权益名称（会员等级、是否内部员工、vip等）
	 */
	private String rightName;
	/**
	 * 权益值
	 */
	private String rightValue;
	/**
	 * 权益描述
	 */
	private String rightDesc;
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
