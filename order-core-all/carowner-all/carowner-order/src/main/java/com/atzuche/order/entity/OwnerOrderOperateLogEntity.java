package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 车主订单操作记录表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:07:01
 * @Description:
 */
@Data
public class OwnerOrderOperateLogEntity implements Serializable {
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
	private String ownerOrderNo;
	/**
	 * 操作人会员号
	 */
	private String memNo;
	/**
	 * 操作类型
	 */
	private Integer operateType;
	/**
	 * 参数
	 */
	private String param;
	/**
	 * 操作描述
	 */
	private String operateDesc;
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
