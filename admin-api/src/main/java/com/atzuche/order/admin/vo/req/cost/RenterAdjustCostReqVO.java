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
@Data
@ToString
public class RenterAdjustCostReqVO {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
    @ApiModelProperty(value="租客子订单号",required=true)
//    @NotBlank(message="renterOrderNo不能为空")
    private String renterOrderNo;
    
    @ApiModelProperty(value="车主子订单号",required=true)
//    @NotBlank(message="ownerOrderNo不能为空")
    private String ownerOrderNo;
    
    @ApiModelProperty(value="租客给车主的调价",required=true)
//    @NotBlank(message="租客给车主的调价不能为空")
    private String renterToOwnerAdjustAmt;
    
    @ApiModelProperty(value="车主给租客的调价",required=true)
//    @NotBlank(message="车主给租客的调价不能为空")
    private String ownerToRenterAdjustAmt;
    
    
    
}