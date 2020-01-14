package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 主订单表
 * 
 * @author ZhangBin
 * @date 2020-01-08 20:40:42
 * @Description:
 */
@Data
public class OrderDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
		/**
         * 主订单号
         */
		@AutoDocProperty("主订单号")
        private String orderNo;
    	/**
         * 租客会员号
         */
    	@AutoDocProperty("租客会员号")
        private String memNoRenter;
    	/**
         * 订单类型（内部分类）1：短租， 2：套餐
         */
    	@AutoDocProperty("订单类型")
        private Integer category;
    	/**
         * 场景号
         */
    	@AutoDocProperty("场景号")
        private String entryCode;
    	/**
         * 来源 1：手机，2：网站，3:管理后台，4:cp b2c, 5:cp upop
         */
    	@AutoDocProperty("来源 1：手机，2：网站，3:管理后台，4:cp b2c, 5:cp upop")
        private String source;
    	/**
         * 预计起租时间
         */
    	@AutoDocProperty("预计起租时间")
        private LocalDateTime expRentTime;
    	/**
         * 预计还车时间
         */
    	@AutoDocProperty("预计还车时间")
        private LocalDateTime expRevertTime;
    	/**
         * 下单城市名称
         */
    	@AutoDocProperty("下单城市名称")
        private String cityName;
    	/**
         * 下单城市code
         */
    	@AutoDocProperty("下单城市code")
        private String cityCode;
    	/**
         * 是否出市 0-否，1-是
         */
    	@AutoDocProperty("是否出市")
        private Integer isOutCity;
        /**
         * 租车城市（用车城市）
         */
        @AutoDocProperty("租车城市（用车城市）")
        private String rentCity;
    	/**
         * 是否免押 0-否，1-是
         */
    	@AutoDocProperty("是否免押")
        private Integer isFreeDeposit;
    	/**
         * 是否使用机场服务 0-否，1-是
         */
    	@AutoDocProperty("是否使用机场服务")
        private Integer isUseAirPortService;
    	/**
         * 航班号
         */
    	@AutoDocProperty("航班号")
        private String flightId;
    	/**
         * 请求时间
         */
    	@AutoDocProperty("请求时间")
        private LocalDateTime reqTime;
    	/**
         * 限时立减金额（面额）
         */
    	@AutoDocProperty("限时立减金额")
        private Integer limitAmt;
    	/**
         * 风控审核id
         */
    	@AutoDocProperty("风控审核id")
        private Integer riskAuditId;
    		/**
         * 订单图片存储目录
         */
    		@AutoDocProperty("订单图片存储目录")
        private String basePath;
    					
}
