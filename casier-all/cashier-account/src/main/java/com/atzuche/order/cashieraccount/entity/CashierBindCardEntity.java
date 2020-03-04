package com.atzuche.order.cashieraccount.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.ToString;


/**
 * 个人免押绑卡信息表
 * 
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 * @Description:
 */
@Data
@ToString
public class CashierBindCardEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 银行卡号
	 */
	private String cardNo;
	/**
	 * 银行code
	 */
	private Integer bankCode;
	/**
	 * 银行名称
	 */
	private String bankName;
	/**
	 * 银行卡类型（储蓄卡，信用卡）
	 */
	private Integer bankType;
	/**
	 * 绑卡人手机号
	 */
	private String phone;
	/**
	 * 绑卡人名称
	 */
	private String name;
	/**
	 * 绑卡人身份证件类型
	 */
	private Integer cardIdentityType;
	/**
	 * 证件号
	 */
	private String cardIdentityNo;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 
	 */
	private String createOp;
	/**
	 * 
	 */
	private LocalDateTime updateTime;
	/**
	 * 
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
