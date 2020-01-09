package com.atzuche.order.open.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * 商品概览价格明细表
 * 
 * @author ZhangBin
 * @date 2020-01-08 20:40:42
 * @Description:
 */
@Data
public class RenterGoodsPriceDetailDTO implements Serializable {
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
         * 商品概览id
         */
        private Integer goodsId;
    	/**
         * 天
         */
        private LocalDate carDay;
    	/**
         * 天单价
         */
        private Integer carUnitPrice;
    	/**
         * 小时数
         */
        private Float carHourCount;
    	/**
         * 还车时间
         */
        private LocalDateTime revertTime;
    					
}
