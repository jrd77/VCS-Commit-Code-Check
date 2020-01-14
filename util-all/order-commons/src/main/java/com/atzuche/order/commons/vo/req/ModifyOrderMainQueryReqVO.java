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
public class ModifyOrderMainQueryReqVO {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
	@NotBlank(message="memNo不能为空")
	@AutoDocProperty(value="memNo,必填，",required=true)
	private String memNo;
}
