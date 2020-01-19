package com.atzuche.order.open.vo.request;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransferReq implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3881151701078148711L;
	
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;

	@AutoDocProperty(value="租客memNo",required=true)
	private String memNo;
	
	@NotBlank(message="车辆注册号carNo不能为空")
	@AutoDocProperty(value="车辆注册号",required=true)
    private String carNo;
	
	@AutoDocProperty(value="是否使用特供价 1-使用，0-不使用")
    private Integer useSpecialPriceFlag;
	
	@AutoDocProperty(value="管理后台操作人")
    private String operator;
}
