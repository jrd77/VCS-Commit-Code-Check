/**
 * 
 */
package com.atzuche.order.commons.vo.rentercost;

import org.hibernate.validator.constraints.NotBlank;

import com.autoyol.doc.annotation.AutoDocProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author jing.huang
 *
 */
@Data
@ToString
public class RenterCostReqVO {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
	//根据的是订单号和会员号查询的。下面两个子订单号参数实际并未用到。
    @ApiModelProperty(value="租客子订单号",required=true)
//    @NotBlank(message="renterOrderNo不能为空")
    private String renterOrderNo;
    
    @ApiModelProperty(value="车主子订单号",required=true)
//    @NotBlank(message="ownerOrderNo不能为空")
    private String ownerOrderNo;
}
