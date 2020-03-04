package com.atzuche.order.commons.vo.req;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccountOwnerCashExamineReqVO {

	@NotBlank(message="提现金额不能为空")
	@Pattern(regexp="^\\d*$",message="提现金额必须为数字")
	@Max(value=10000,message="提现金额不能超过1万")
	private String amt;//提现金额	string	必填，对应member表balance
	
	@NotBlank(message="卡号标识不能为空")
	private String cardId;//卡号标识	string	必填，数据库中对应id字段
	
	@NotBlank(message="memNo不能为空")
	private String memNo;//用户token	string	必填
	/**
	 * 一天最大提现次数
	 */
	private Integer limitSize;
	/**
	 * 最少提现金额
	 */
	private Integer cashMinAmt;
	
}
