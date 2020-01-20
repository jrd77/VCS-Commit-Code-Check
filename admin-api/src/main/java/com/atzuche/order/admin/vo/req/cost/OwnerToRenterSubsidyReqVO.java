/**
 * 
 */
package com.atzuche.order.admin.vo.req.cost;

import org.hibernate.validator.constraints.NotBlank;

import com.autoyol.doc.annotation.AutoDocProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author jing.huang
 *
 */
@ToString
@Data
public class OwnerToRenterSubsidyReqVO {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
    @ApiModelProperty(value="车主子订单号",required=true)
    @NotBlank(message="ownerOrderNo不能为空")
    private String ownerOrderNo;
    
    @AutoDocProperty("租金补贴")
    private String rentAmt;
    
    
}
