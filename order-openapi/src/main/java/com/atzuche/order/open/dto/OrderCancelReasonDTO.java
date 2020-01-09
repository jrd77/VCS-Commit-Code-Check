package com.atzuche.order.open.dto;

import lombok.Data;

import java.io.Serializable;


/**
 * 订单取消原因
 * 
 * @author ZhangBin
 * @date 2020-01-08 20:41:54
 * @Description:
 */
@Data
public class OrderCancelReasonDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
		/**
         * 主订单号
         */
        private String orderNo;
    	/**
         * 取消方 1-车主、2-租客、3-平台
         */
        private Integer cancelSource;
    	/**
         * 1：租客责任，2：车主责任，；6. 双方无责、平台承担保险。
         */
        private Integer dutySource;
    	/**
         * 取消原因
         */
        private String cancelReason;
    					
}
