package com.atzuche.order.renterwz.entity;

import lombok.Data;

import java.util.Date;

/**
 * MqSendFeelbackLogEntity
 *
 * @author shisong
 * @date 2019/12/28
 */
@Data
public class MqSendFeelbackLogEntity{
	/**
	*主键
	*/
	private Long id;
	/**
	*MQ消息ID
	*/
	private String msgId;
	/**
	*序列名称
	*/
	private String queueName;
	/**
	*发送消息内容，JSON格式
	*/
	private String msg;
	/**
	*消息状态反馈状态
	*/
	private String status;
	/**
	*发送时间
	*/
	private Date sendTime;
	private Date feelbackTime;
	/**
	*返回码
	*/
	private String feelbackCode;
	/**
	*返回消息（错误，返回错误描述）
	*/
	private String feelbackMsg;
	private Date createTime;
	private Date updateTime;
}
