/**
 * 
 */
package com.atzuche.order.commons.vo.req;

import org.hibernate.validator.constraints.NotBlank;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author jing.huang
 *
 */
@Data
@ToString
public class ModifyOrderQueryReqVO {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
	@NotBlank(message="租客子订单编号不能为空")
	@AutoDocProperty(value="租客子订单编号,必填，",required=true)
	private String renterOrderNo;
	
}
