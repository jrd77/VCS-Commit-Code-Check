/**
 * 
 */
package com.atzuche.order.commons.vo.rentercost;

import org.hibernate.validator.constraints.NotBlank;

import com.autoyol.doc.annotation.AutoDocProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jing.huang
 *
 */
@Data
public class RenterFineCostReqVO {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
    @ApiModelProperty(value="租客子订单号",required=true)
    @NotBlank(message="renterOrderNo不能为空")
    private String renterOrderNo;
    
	@AutoDocProperty(value="租客提前还车罚金")
	private String renterBeforeReturnCarFineAmt;
	
	@AutoDocProperty(value="租客延迟还车罚金")
	private String renterDelayReturnCarFineAmt;
	// 操作人
	private String operatorName;
    
}
