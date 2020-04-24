package com.atzuche.order.sms.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @date 2020-04-02 20:15:10
 * @Description:
 */
@Data
@ToString
public class SmsMsgSendLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 短消息类型
	 */
	private Integer msgType;
	/**
	 * 短消息参数内容
	 */
	private String sendParams;
	/**
	 * 发送时间
	 */
	private LocalDateTime sendTime;
	/**
	 * 发送次数
	 */
	private Integer sendTotal;
	/**
	 * 是否已发送 0：否 1：是
	 */
	private Integer status;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 更新时间
	 */
	private LocalDateTime updateTime;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
