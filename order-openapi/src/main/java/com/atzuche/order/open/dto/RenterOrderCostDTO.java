package com.atzuche.order.open.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租客订单费用总表
 * 
 * @author ZhangBin
 * @date 2020-01-08 20:40:42
 * @Description:
 */
@Data
public class RenterOrderCostDTO implements Serializable {
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
         * 租金
         */
        private Integer rentCarAmount;
    	/**
         * 佣金费用
         */
        private Integer commissionAmount;
    	/**
         * 基础保障费用
         */
        private Integer basicEnsureAmount;
    	/**
         * 全面保障费用
         */
        private Integer comprehensiveEnsureAmount;
    	/**
         * 附加驾驶人保障费用
         */
        private Integer additionalDrivingEnsureAmount;
    	/**
         * 其他费用
         */
        private Integer otherAmount;
    	/**
         * 平台补贴费用
         */
        private Integer platformSubsidyAmount;
    	/**
         * 车主补贴费用
         */
        private Integer carOwnerSubsidyAmount;
    						
}
