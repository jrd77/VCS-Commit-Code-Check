/**
 * 
 */
package com.atzuche.order.admin.vo.req.cost;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

import com.atzuche.order.commons.entity.dto.CommUseDriverInfoSimpleDTO;
import com.autoyol.doc.annotation.AutoDocProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jing.huang
 *
 */
@Data
public class AdditionalDriverInsuranceIdsReqVO {
	
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
    @ApiModelProperty(value="租客子订单号",required=true)
    @NotBlank(message="renterOrderNo不能为空")
    private String renterOrderNo;
    
	@AutoDocProperty("附加驾驶人ID列表")
	List<CommUseDriverInfoSimpleDTO> listCommUseDriverIds;
	
}
