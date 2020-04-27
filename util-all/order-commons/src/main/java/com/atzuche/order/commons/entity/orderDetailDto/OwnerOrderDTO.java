package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 车主订单子表
 * 
 * @author ZhangBin
 * @date 2020-01-14 16:42:45
 * @Description:
 */
@Data
public class OwnerOrderDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
		/**
         * 主订单号
         */
        @AutoDocProperty(value="主订单号",required=true)
        private String orderNo;
    	/**
         * 子订单号
         */
        @AutoDocProperty(value="车主子订单号",required=true)
        private String ownerOrderNo;

        /**
         * 租客子订单号
         */
        @AutoDocProperty(value="租客子订单号",required=true)
        private String renterOrderNo;
    	/**
         * 会员号
         */
        @AutoDocProperty(value="车主会员号",required=true)
        private String memNo;
    	/**
         * 显示起租时间
         */
        @AutoDocProperty(value="显示起租时间",required=true)
        private LocalDateTime showRentTime;

    	/**
         * 显示还车时间
         */
        @AutoDocProperty(value="显示还车时间",required=true)
        private LocalDateTime showRevertTime;
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
        @AutoDocProperty("预计还车时间")
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
         * 车主子单状态，1-待补付,2-修改待确认,3-进行中,4-已完结,0-已结束
         */
        @AutoDocProperty(value="车主子单状态，1-待补付,2-修改待确认,3-进行中,4-已完结,0-已结束",required=true)
        private Integer childStatus;
    	/**
         * 修改方 1、后台管理 2、租客 3、车主
         */
        @AutoDocProperty(value="修改方 1、后台管理 2、租客 3、车主",required=true)
        private String changeSource;
    	/**
         * 是否取消 0-正常，1-取消
         */
        @AutoDocProperty(value="是否取消 0-正常，1-取消",required=true)
        private Integer isCancel;
    	/**
         * 是否有效 1-有效 0-无效
         */
        @AutoDocProperty(value="是否有效 1-有效 0-无效",required=true)
        private Integer isEffective;

        @AutoDocProperty("是否有效 1-有效 0-无效")
        private String isEffectiveTxt;
    	/**
         * 是否使用特供价 0-否，1-是
         */
        @AutoDocProperty(value="是否使用特供价 0-否，1-是",required=true)
        private Integer isUseSpecialPrice;
    	/**
         * 待调度原因 1、车主主动拒单 2、超时拒单 3、车主取消订单
         */
        @AutoDocProperty(value="待调度原因 1、车主主动拒单 2、超时拒单 3、车主取消订单",required=true)
        private String dispatchReason;
    /**
     * 订单是否查看,1:是，0：否
     */
        @AutoDocProperty(value = "订单是否查看,1:是，0：否")
        private Integer seeFlag;

    	/**
         * 取消者
         */
        @AutoDocProperty(value="取消者",required=true)
        private String cancleOp;
        /**
         * 创建时间
         */
        @AutoDocProperty(value="创建时间",required=true)
        private LocalDateTime createTime;
    					
}
