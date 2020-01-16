package com.atzuche.order.mq.common.base;

import lombok.*;

import java.io.Serializable;

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
     *  必传参数（发送内容）
     */
	private String context;
    /**
     * 必传参数(发送目的地手机号)
     */
	private String phone;
}
