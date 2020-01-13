package com.atzuche.order.flow.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租客端交易流程表
 * 
 * @author ZhangBin
 * @date 2020-01-01 15:10:51
 */
@Data
public class OrderFlowEntity implements Serializable {

    private static final long serialVersionUID = 9032563652328041085L;

    /**
	 * 
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 交易状态
	 */
	private Integer orderStatus;
	/**
	 * 状态描述
	 */
	private String orderStatusDesc;

	/**
	 * 来源，如管理后台
	 */
	private String source;
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
