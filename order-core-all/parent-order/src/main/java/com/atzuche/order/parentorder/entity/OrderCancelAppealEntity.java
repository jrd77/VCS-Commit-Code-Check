package com.atzuche.order.parentorder.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 
 * 
 * @author ZhangBin
 * @date 2020-03-02 11:10:10
 * @Description:
 */
@Data
public class OrderCancelAppealEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 取消订单申述id
	 */
	private Integer id;
	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 租客/车主订单号(与取消方保持一致)
	 */
	private String subOrderNo;
	/**
	 * 会员号
	 */
	private Integer memNo;
	/**
	 * 用户角色, 1-车主 2-租客 3-平台 4-平台代租客取消 5-平台代车主取消
	 */
	private Integer memRole;
	/**
	 * 申述原因
	 */
	private String appealReason;
	/**
	 * 是否已进行责任判定 0-未判定，1-已判定
	 */
	private Integer isWrongdoer;
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
