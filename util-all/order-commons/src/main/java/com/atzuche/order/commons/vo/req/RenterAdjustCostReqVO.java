/**
 * 
 */
package com.atzuche.order.commons.vo.req;

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
    // 操作人
    private String operateName;

    @AutoDocProperty("调价原因类型")
    @NotBlank(message = "调价原因类型不能为空")
    private Integer adjustReasonType;

    @AutoDocProperty("调价原因描述")
    @NotBlank(message = "调价原因描述不能为空")
    private String adjustReasonDesc;

    @AutoDocProperty("当调价原因类型为【其他】时的备注内容")
    private String remarkContent;
    
}
