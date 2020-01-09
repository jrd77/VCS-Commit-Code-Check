package com.atzuche.order.open.dto;

import lombok.Data;

import java.io.Serializable;


/**
 * 车主端采购费用明细表
 * 
 * @author ZhangBin
 * @date 2020-01-08 20:47:41
 * @Description:
 */
@Data
public class OwnerOrderPurchaseDetailDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
		/**
         * 主订单号
         */
        private String orderNo;
    	/**
         * 子订单号
         */
        private String ownerOrderNo;
    	/**
         * 会员号
         */
        private String memNo;
    	/**
         * 费用编码费用编码,租金
         */
        private String costCode;
    	/**
         * 费用描述
         */
        private String costCodeDesc;
    	/**
         * 单价
         */
        private Integer unitPrice;
    	/**
         * 份数
         */
        private Double count;
    	/**
         * 金额
         */
        private Integer totalAmount;
    					
}
