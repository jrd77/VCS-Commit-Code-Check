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
public class RenterToPlatformCostReqVO {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
    @ApiModelProperty(value="租客子订单号",required=true)
    @NotBlank(message="renterOrderNo不能为空")
    private String renterOrderNo;
    
    @AutoDocProperty("超里程费用")
    private String extraMileageFee;
    @AutoDocProperty("油费")
    private String oilFee;
    @AutoDocProperty("车辆清洗费")
    private String carWashFee;
    @AutoDocProperty("停车费")
    private String stayCarFee;
    @AutoDocProperty("超时费")
    private String overTimeFee;
    @AutoDocProperty("临时修改订单时间、地址")
    private String temporaryModifyOrderFee;
    @AutoDocProperty("延误等待费")
    private String delayWaitFee;
}
