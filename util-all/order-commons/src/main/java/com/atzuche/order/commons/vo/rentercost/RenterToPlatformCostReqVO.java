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
public class RenterToPlatformCostReqVO {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
    @ApiModelProperty(value="租客子订单号",required=true)
    @NotBlank(message="renterOrderNo不能为空")
    private String renterOrderNo;
    
    @AutoDocProperty("油费")
    private String oliAmt;
    @AutoDocProperty("超时费用")
    private String timeOut;
    @AutoDocProperty("临时修改订单的时间和地址")
    private String modifyOrderTimeAndAddrAmt;
    @AutoDocProperty("车辆清洗费")
    private String carWash;
    @AutoDocProperty("延误等待费")
    private String dlayWait;
    @AutoDocProperty("停车费")
    private String stopCar;
    @AutoDocProperty("超里程费用")
    private String extraMileage;
    // 操作人
    private String operatorName;
}
