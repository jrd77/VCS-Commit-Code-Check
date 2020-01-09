package com.atzuche.order.commons.entity.orderDetailDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 车主会员概览表
 * 
 * @author ZhangBin
 * @date 2020-01-08 20:40:42
 * @Description:
 */
@Data
public class OwnerMemberDTO implements Serializable {
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
         * 会员类型
         */
        private Integer memType;
    	/**
         * 手机号
         */
        private String phone;
    	/**
         * 头像
         */
        private String headerUrl;
    	/**
         * 真实姓名
         */
        private String realName;
    	/**
         * 昵称
         */
        private String nickName;
    	/**
         * 成功下单次数
         */
        private Integer orderSuccessCount;
    	/**
         * 平台上架车辆数
         */
        private Integer renterCarCount;
    					
}
