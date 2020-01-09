package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 租客订单子表
 * 
 * @author ZhangBin
 * @date 2020-01-08 20:40:42
 * @Description:
 */
@Data
public class RenterOrderDTO implements Serializable {
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
         * 车主是否同意 0-未处理，1-已同意，2-已拒绝
         */
        private Integer agreeFlag;
    	/**
         * 车主同意交易请求的时间
         */
        private LocalDateTime reqAcceptTime;
    	/**
         * 租客子单状态，1-待补付,2-修改待确认,3-进行中,4-已完结,5-已结束
         */
        private Integer childStatus;

    	/**
         * 修改方 1、后台管理 2、租客 3、车主
         */
        private String changeSource;
    	/**
         * 是否使用凹凸币 0-否，1-是
         */
        private Integer isUseCoin;
    	/**
         * 是否使用钱包 0-否，1-是
         */
        private Integer isUseWallet;
    	/**
         * 是否使用优惠券 0-否，1-是
         */
        private Integer isUseCoupon;
    	/**
         * 是否使用取车服务 0-否，1-是
         */
        private Integer isGetCar;
    	/**
         * 是否使用还车服务 0-否，1-是
         */
        private Integer isReturnCar;
    	/**
         * 附加驾驶人（人数）
         */
        private Integer addDriver;
    	/**
         * 是否开启不计免赔 0-不开启，1-开启
         */
        private Integer isAbatement;
    	/**
         * 是否有效 1-有效 0-无效
         */
        private Integer isEffective;
    	/**
         * 是否使用特供价 0-否，1-是
         */
        private Integer isUseSpecialPrice;
    	/**
         * 是否取消 0-正常，1-取消
         */
        private Integer isCancle;
    					
}
