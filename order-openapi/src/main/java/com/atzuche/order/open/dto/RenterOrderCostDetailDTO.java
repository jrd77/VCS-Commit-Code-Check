package com.atzuche.order.open.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 租客费用明细表
 * 
 * @author ZhangBin
 * @date 2020-01-08 20:46:50
 * @Description:
 */
@Data
public class RenterOrderCostDetailDTO implements Serializable {
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
         * 费用编码
         */
        private String costCode;
    	/**
         * 费用描述
         */
        private String costDesc;
    	/**
         * 开始时间
         */
        private LocalDateTime startTime;
    	/**
         * 结束时间
         */
        private LocalDateTime endTime;
    	/**
         * 单价
         */
        private Integer unitPrice;
    	/**
         * 份数
         */
        private Double count;
    	/**
         * 总价
         */
        private Integer totalAmount;
    	/**
         * 部门ID
         */
        private Integer deptId;
    		/**
         * 操作人ID
         */
        private String operatorId;
    	/**
         * 操作人名称
         */
        private String operator;
    	/**
         * 备注
         */
        private String remark;
    					
}
