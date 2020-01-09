package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 车主收益资金进出明细表
 * 
 * @author ZhangBin
 * @date 2020-01-08 20:48:34
 * @Description:
 */
@Data
public class AccountOwnerIncomeDetailDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
		/**
         * 会员号
         */
        private String memNo;
    	/**
         * 主订单号
         */
        private String orderNo;
    	/**
         * 收益金额
         */
        private Integer amt;
    	/**
         * 收益/提现时间
         */
        private LocalDateTime time;
    	/**
         * 收益描述
         */
        private String detail;
    	/**
         * 收益费用编码
         */
        private Integer costCode;
    	/**
         * 收益费用编码描述
         */
        private String costDetail;
    	/**
         * 收益类型（收益/提现）
         */
        private Integer type;
    	/**
         * 提现申请id
         */
        private Integer cashApplyId;
    	/**
         * 收益审核id
         */
        private Integer incomeExamineId;
    	/**
         * 收银台凭证
         */
        private String unqueNo;
    					
}
