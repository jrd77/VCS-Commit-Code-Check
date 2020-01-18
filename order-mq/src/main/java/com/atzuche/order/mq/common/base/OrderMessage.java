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

	private T message;
    /**
     *  参数（发送内容 废弃）
     */
	private String context;
    /**
     * 参数(发送目的地手机号 废弃)
     */
	private String phone;
	/**
	 * 发送短信的Map构成（对应的发送短信模版ID，对应的短信内容参数）
	 */
	private Map map;
}
