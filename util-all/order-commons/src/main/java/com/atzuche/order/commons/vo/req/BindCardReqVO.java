package com.atzuche.order.commons.vo.req;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BindCardReqVO {
	@NotBlank(message="银行编号不能为空") 
	@Pattern(regexp="^[0-9]*$",message="银行编号只能是数字")
	private String bank;//银行编号
	
	@NotBlank(message="卡号不能为空") 
	@Pattern(regexp="^[0-9]*$",message="银行卡号只能是数字")
	@Length(max=19,min=8,message="银行卡号的格式不正确")
	private String cardNo;//卡号
	 
	@NotBlank(message="memNo不能为空") 
	private String memNo;
	
	@NotBlank(message="持卡人姓名不能为空") 
	private String cardHolder;//持卡人姓名
	
	@Length(max=30,message="开户支行名称最多为30个中文字符 ")
 	private String branchBankName;//开户支行名称	
		
	@NotBlank(message="开户城市不能为空") 
 	private String city;//开户城市
	
	@NotBlank(message="开户城市编号不能为空") 
 	private String cityCode;//开户城市编号	
	
	@NotBlank(message="开户省份不能为空") 
 	private String province;//开户省份	

}
