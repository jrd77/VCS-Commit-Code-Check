/**
 * 
 */
package com.atzuche.order.commons.vo.req;

import org.hibernate.validator.constraints.NotBlank;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;

/**
 * @author jing.huang
 *
 */
@Data
public class OrderCostReqVO {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
	//@NotBlank(message="memNo不能为空")
	@AutoDocProperty(value="memNo,必填，",required=true)
	private String memNo;
	
	@NotBlank(message="子订单(租客或车主)编号不能为空")
	@AutoDocProperty(value="子订单(租客或车主)编号,必填，",required=true)
	private String subOrderNo;
	
	
}
