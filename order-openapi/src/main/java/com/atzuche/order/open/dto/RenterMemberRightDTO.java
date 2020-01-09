package com.atzuche.order.open.dto;

import lombok.Data;

import java.io.Serializable;


/**
 * 租客端会员权益表
 * 
 * @author ZhangBin
 * @date 2020-01-08 20:46:11
 * @Description:
 */
@Data
public class RenterMemberRightDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
		/**
         * 主订单号
         */
        private String orderNo;
    	/**
         * 子订单号
         */
        private String renterOrderNo;
    	/**
         * 会员号
         */
        private String memNo;
    	/**
         * 权益编码
         */
        private String rightCode;
    	/**
         * 权益名称（会员等级、是否内部员工、vip等）
         */
        private String rightName;
    	/**
         * 权益值
         */
        private String rightValue;
    	/**
         * 权益类别 1-内部员工类别，2-会员标志类别，3-太保会员类别，4-任务类别
         */
        private Integer rightType;
    	/**
         * 权益描述
         */
        private String rightDesc;
    					
}
