package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * 租客端会员概览表
 * 
 * @author ZhangBin
 * @date 2020-01-14 16:45:40
 * @Description:
 */
@Data
public class RenterMemberDTO implements Serializable {
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
         * 会员号
         */
        @AutoDocProperty(value="会员号",required=true)
        private String memNo;
    	/**
         * 手机号
         */
        @AutoDocProperty(value="手机号",required=true)
        private String phone;
    	/**
         * 头像
         */
        @AutoDocProperty(value="头像",required=true)
        private String headerUrl;
    	/**
         * 真实姓名
         */
        @AutoDocProperty(value="真实姓名",required=true)
        private String realName;
    	/**
         * 昵称
         */
        @AutoDocProperty(value="昵称",required=true)
        private String nickName;
    	/**
         * 驾驶证初次领证日期
         */
        @AutoDocProperty(value="驾驶证初次领证日期",required=true)
        private LocalDate certificationTime;
    	/**
         * 成功下单次数
         */
        @AutoDocProperty(value="成功下单次数",required=true)
        private Integer orderSuccessCount;
    	/**
         * 是否可以租车：1：可租，0：不可租
         */
        @AutoDocProperty(value="是否可以租车：1：可租，0：不可租",required=true)
        private Integer rentFlag;
    	/**
         * 姓
         */
        @AutoDocProperty(value="姓",required=true)
        private String firstName;
    	/**
         * 性别(1:男、2：女)
         */
        @AutoDocProperty(value="性别(1:男、2：女)",required=true)
        private Integer gender;
        /**
         * 身份证号码
         */
        @AutoDocProperty("身份证号码")
        private String idNo;
    	/**
         * 身份证认证 0：未上传，1：已上传，2：已认证，3：认证不通过, 4:无效数据
         */
        @AutoDocProperty(value="身份证认证 0：未上传，1：已上传，2：已认证，3：认证不通过, 4:无效数据",required=true)
        private Integer idCardAuth;
    	/**
         * 驾照认证 0：未上传，1：已上传，2：已认证，3：认证不通过, 4:无效数据
         */
        @AutoDocProperty(value="驾照认证 0：未上传，1：已上传，2：已认证，3：认证不通过, 4:无效数据",required=true)
        private Integer driLicAuth;
    	/**
         * 驾驶增副页 0：未上传，1：已上传，2：已认证，3：认证不通过, 4:无效数据, 5:未上传（已认证）
         */
        @AutoDocProperty(value="驾驶增副页 0：未上传，1：已上传，2：已认证，3：认证不通过, 4:无效数据, 5:未上传（已认证）",required=true)
        private Integer driViceLicAuth;
    	/**
         * 核查状态 0-未查核 1-核查通过 2-核查不通过 3-核查已通过，有劣迹
         */
        @AutoDocProperty(value="核查状态 0-未查核 1-核查通过 2-核查不通过 3-核查已通过，有劣迹",required=true)
        private Integer renterCheck;
    	/**
         * 租客注册时间
         */
        @AutoDocProperty(value="租客注册时间",required=true)
        private LocalDateTime regTime;
    	/**
         * 会员来源
         */
        @AutoDocProperty(value="会员来源",required=true)
        private String outerSource;
    					
}
