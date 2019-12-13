package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租客端违章处理事件表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:23:44
 * @Description:
 */
@Data
public class RenterEventWzEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private Long orderNo;
	/**
	 * 子订单号
	 */
	private String renterOrderNo;
	/**
	 * 事件类型
	 */
	private Integer eventType;
	/**
	 * 事件名称
	 */
	private String eventName;
	/**
	 * 事件描述
	 */
	private String eventDesc;
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
