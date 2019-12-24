package com.atzuche.order.commons.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * rabbitmq接收消息记录
 * 
 * @author ZhangBin
 * @date 2019-12-23 19:27:14
 * @Description:
 */
@Data
public class RabbitMsgLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * mq消息唯一key
	 */
	private String mqKey;
	/**
	 * mq交换器类型
	 */
	private String mqExchange;
	/**
	 * mq绑定队列
	 */
	private String mqQueue;
	/**
	 * 是否消费 1：已消费  0未消费
	 */
	private Integer isConsume;
	/**
	 * 业务类型; 1下单支付收银台,2 收银台退款 。。。。。
	 */
	private Integer businessType;
	/**
	 * 业务唯一流水id
	 */
	private Integer uniqueId;
	/**
	 * 消费mq内容 json字符串
	 */
	private String mqMsg;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

}
