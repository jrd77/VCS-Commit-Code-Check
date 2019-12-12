package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租客端违章处理状态表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:23:44
 * @Description:
 */
@Data
public class RenterEventWzStatusEntity implements Serializable {
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
	 * 子订单号
	 */
	private String renterOrderNo;
	/**
	 * 处理状态
	 */
	private Integer status;
	/**
	 * 描述信息
	 */
	private String statusDesc;
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
