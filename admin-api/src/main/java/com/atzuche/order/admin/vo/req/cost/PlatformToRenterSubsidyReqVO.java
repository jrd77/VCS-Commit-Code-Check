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
public class PlatformToRenterSubsidyReqVO {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
    @ApiModelProperty(value="租客子订单号",required=true)
    @NotBlank(message="renterOrderNo不能为空")
    private String renterOrderNo;
    
    @AutoDocProperty(value="升级车辆补贴")
    String dispatchingSubsidy;
	
	@AutoDocProperty(value="油费补贴")
    String oilSubsidy;
	
	@AutoDocProperty(value="洗车费补贴")
    String cleanCarSubsidy;
	
	@AutoDocProperty(value="取还车迟到补贴")
    String getReturnDelaySubsidy;
	
	@AutoDocProperty(value="延时补贴")
    String delaySubsidy;
	
	@AutoDocProperty(value="交通费补贴")
    String trafficSubsidy;
	
	
	@AutoDocProperty(value="基础保障费补贴")
    String insureSubsidy;
	@AutoDocProperty(value="租金补贴")
    String rentAmtSubsidy;
	@AutoDocProperty(value="其他补贴")
    String otherSubsidy;
	@AutoDocProperty(value="全面保障服务费补贴")
    String abatementSubsidy;
	@AutoDocProperty(value="手续费补贴")
    String feeSubsidy;
}
