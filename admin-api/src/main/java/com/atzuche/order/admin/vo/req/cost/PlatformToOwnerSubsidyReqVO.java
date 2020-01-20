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
public class PlatformToOwnerSubsidyReqVO {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
    @ApiModelProperty(value="车主子订单号",required=true)
    @NotBlank(message="ownerOrderNo不能为空")
    private String ownerOrderNo;
    
    @AutoDocProperty("超里程费用")
    private String mileageAmt;
    @AutoDocProperty("油费补贴")
    private String oilSubsidyAmt;
    @AutoDocProperty("洗车费补贴")
    private String washCarSubsidyAmt;
    @AutoDocProperty("车上物品损失补贴")
    private String CarGoodsLossSubsidyAmt;
    @AutoDocProperty("延时补贴")
    private String delaySubsidyAmt;
    @AutoDocProperty("交通补贴")
    private String trafficSubsidyAmt;
    @AutoDocProperty("收益补贴")
    private String incomeSubsidyAmt;
    @AutoDocProperty("其他补贴")
    private String otherSubsidyAmt;
    
}
