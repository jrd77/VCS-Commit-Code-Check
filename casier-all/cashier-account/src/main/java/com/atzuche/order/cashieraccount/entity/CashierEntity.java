package com.atzuche.order.cashieraccount.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 收银表
 * 
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 * @Description:
 */
@Data
public class CashierEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 
	 */
	private Long orderNo;
	/**
	 * 会员号
	 */
	private Integer memNo;
	/**
	 * 费用类型
	 */
	private Integer costType;
	/**
	 * 支付来源（线上线下）
	 */
	private Integer paySource;
	/**
	 * 支付方式（消费、预授权、信用支付）
	 */
	private Integer payment;
	/**
	 * 支付渠道
	 */
	private Integer payChannel;
	/**
	 * 支付类型
	 */
	private Integer payType;
	/**
	 * 支付凭证
	 */
	private String payTransNo;
	/**
	 * 支付时间
	 */
	private LocalDateTime payTime;
	/**
	 * 是否补付
	 */
	private Integer isAgainPay;
	/**
	 * 补付次数
	 */
	private Integer againPayNum;
	/**
	 * 签名串
	 */
	private String sign;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 创建人
	 */
	private String createOp;
	/**
	 * 修改时间
	 */
	private LocalDateTime updateTime;
	/**
	 * 修改人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
