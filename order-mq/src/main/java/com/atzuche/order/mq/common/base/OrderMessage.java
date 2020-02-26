package com.atzuche.order.mq.common.base;

import lombok.*;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 胡春林
 * 消息结构体
 * @param <T>
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderMessage<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    /**
     * 需要发送的对象数据
     */
	private T message;

	/**
	 * 发送短信的Map构成（对应的发送短信模版ID，对应的短信内容参数）
	 */
	private Map map;
}
