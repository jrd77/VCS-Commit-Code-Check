package com.atzuche.order.commons.entity.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BankCardDTO {

	private String id;
	
	private String cardNo;//卡号
	
	private String cardHolder;//持卡人
	
	private String bankName;//银行编码
	
	private String addCert;//是否已添加证件，1；是，0：否

	private String memNo;//用户注册号
	
	private String branchBankName;//开户支行名
	
	private String province;//开户省份
	
	private String city;//开户城市
}
