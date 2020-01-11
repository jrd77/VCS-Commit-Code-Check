package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 暂扣费用总表
 * 
 * @author ZhangBin
 * @date 2020-01-11 16:41:19
 * @Description:
 */
@Data
public class AccountRenterDetainCostDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
		/**
         * 主订单号
         */
        @AutoDocProperty(value="主订单号",required=true)
        private String orderNo;
    	/**
         * 会员号
         */
        @AutoDocProperty(value="会员号",required=true)
        private String memNo;
    	/**
         * 暂扣金额
         */
        @AutoDocProperty(value="暂扣金额",required=true)
        private Integer amt;
    						
}
