package com.atzuche.order.commons.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * rabbitmq接收消息记录
 * 
 * @author ZhangBin
 * @date 2019-12-25 14:23:58
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
	 * 业务类型
	 */
	private String businessType;
	/**
	 * 业务唯一流水id
	 */
	private String uniqueNo;
	/**
	 * 消费mq内容 json字符串
	 */
	private String mqMsg;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

}
