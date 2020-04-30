package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 租客订单子表
 * 
 * @author ZhangBin
 * @date 2020-01-14 16:45:21
 * @Description:
 */
@Data
public class RenterOrderDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
		/**
         * 主订单号
         */
        @AutoDocProperty(value="主订单号",required=true)
        private String orderNo;
    	/**
         * 子订单号
         */
        @AutoDocProperty(value="子订单号",required=true)
        private String renterOrderNo;
    	/**
         * 租客会员号
         */
        @AutoDocProperty(value="租客会员号",required=true)
        private String renterMemNo;
    	/**
         * 预计起租时间
         */
        @AutoDocProperty(value="预计起租时间",required=true)
        private LocalDateTime expRentTime;
        @AutoDocProperty("预计起租时间")
        private String expRentTimeStr;
    	/**
         * 预计还车时间
         */
        @AutoDocProperty(value="预计还车时间",required=true)
        private LocalDateTime expRevertTime;


        @AutoDocProperty(value="预计还车时间")
        private String expRevertTimeStr;
    	/**
         * 实际起租时间
         */
        @AutoDocProperty(value="实际起租时间",required=true)
        private LocalDateTime actRentTime;
    	/**
         * 实际还车时间
         */
        @AutoDocProperty(value="实际还车时间",required=true)
        private LocalDateTime actRevertTime;
    	/**
         * 商品编码
         */
        @AutoDocProperty(value="商品编码",required=true)
        private String goodsCode;
    	/**
         * 商品类型
         */
        @AutoDocProperty(value="商品类型",required=true)
        private String goodsType;
    	/**
         * 车主是否同意 0-未处理，1-已同意，2-已拒绝
         */
        @AutoDocProperty(value="车主是否同意 0-未处理，1-已同意，2-已拒绝",required=true)
        private Integer agreeFlag;
    	/**
         * 车主同意交易请求的时间
         */
        @AutoDocProperty(value="车主同意交易请求的时间",required=true)
        private LocalDateTime reqAcceptTime;
    	/**
         * 租客子单状态，1-待补付,2-修改待确认,3-进行中,4-已完结,0-已结束
         */
        @AutoDocProperty(value="租客子单状态，1-待补付,2-修改待确认,3-进行中,4-已完结,0-已结束",required=true)
        private Integer childStatus;
    	/**
         * 修改方 1、后台管理 2、租客 3、车主
         */
        @AutoDocProperty(value="修改方 1、后台管理 2、租客 3、车主",required=true)
        private String changeSource;
    	/**
         * 是否使用凹凸币 0-否，1-是
         */
        @AutoDocProperty(value="是否使用凹凸币 0-否，1-是",required=true)
        private Integer isUseCoin;
    	/**
         * 是否使用钱包 0-否，1-是
         */
        @AutoDocProperty(value="是否使用钱包 0-否，1-是",required=true)
        private Integer isUseWallet;
    	/**
         * 是否使用优惠券 0-否，1-是
         */
        @AutoDocProperty(value="是否使用优惠券 0-否，1-是",required=true)
        private Integer isUseCoupon;
    	/**
         * 是否使用取车服务 0-否，1-是
         */
        @AutoDocProperty(value="是否使用取车服务 0-否，1-是",required=true)
        private Integer isGetCar;
    	/**
         * 是否使用还车服务 0-否，1-是
         */
        @AutoDocProperty(value="是否使用还车服务 0-否，1-是",required=true)
        private Integer isReturnCar;
    	/**
         * 附加驾驶人（人数）
         */
        @AutoDocProperty(value="附加驾驶人（人数）",required=true)
        private Integer addDriver;
    	/**
         * 是否开启不计免赔 0-不开启，1-开启
         */
        @AutoDocProperty(value="是否开启不计免赔 0-不开启，1-开启",required=true)
        private Integer isAbatement;
    	/**
         * 是否有效 1-有效 0-无效
         */
        @AutoDocProperty(value="是否有效 1-有效 0-无效",required=true)
        private Integer isEffective;

        @AutoDocProperty(value="是否有效 1-有效 0-无效",required=true)
        private String isEffectiveTxt;
    	/**
         * 是否使用特供价 0-否，1-是
         */
        @AutoDocProperty(value="是否使用特供价 0-否，1-是",required=true)
        private Integer isUseSpecialPrice;
    	/**
         * 是否取消 0-正常，1-取消
         */
        @AutoDocProperty(value="是否取消 0-正常，1-取消",required=true)
        private Integer isCancle;

        @AutoDocProperty(value = "订单是否查看,1:是，0：否")
        private Integer seeFlag;
    					
}
