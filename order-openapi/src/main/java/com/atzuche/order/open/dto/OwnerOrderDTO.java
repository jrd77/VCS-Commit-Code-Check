package com.atzuche.order.open.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 车主订单子表
 * 
 * @author ZhangBin
 * @date 2020-01-08 20:40:42
 * @Description:
 */
@Data
public class OwnerOrderDTO implements Serializable {
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
         * 显示起租时间
         */
        private LocalDateTime showRentTime;
    	/**
         * 显示还车时间
         */
        private LocalDateTime showRevertTime;
    	/**
         * 预计起租时间
         */
        private LocalDateTime expRentTime;
    	/**
         * 预计还车时间
         */
        private LocalDateTime expRevertTime;
    	/**
         * 实际起租时间
         */
        private LocalDateTime actRentTime;
    	/**
         * 实际还车时间
         */
        private LocalDateTime actRevertTime;
    	/**
         * 商品编码
         */
        private String goodsCode;
    	/**
         * 商品类型
         */
        private String goodsType;
    	/**
         * 车主子单状态，1-待补付,2-修改待确认,3-进行中,4-已完结,5-已结束
         */
        private Integer childStatus;
    	/**
         * 修改方 1、后台管理 2、租客 3、车主
         */
        private String changeSource;
    	/**
         * 是否取消 0-正常，1-取消
         */
        private Integer isCancel;
    	/**
         * 是否有效 1-有效 0-无效
         */
        private Integer isEffective;
    	/**
         * 是否使用特供价 0-否，1-是
         */
        private Integer isUseSpecialPrice;
    	/**
         * 待调度原因 1、车主主动拒单 2、超时拒单 3、车主取消订单
         */
        private String dispatchReason;
    	/**
         * 取消者
         */
        private String cancleOp;
    					
}
