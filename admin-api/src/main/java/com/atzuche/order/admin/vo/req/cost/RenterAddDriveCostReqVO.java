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
public class RenterAddDriveCostReqVO {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
    @ApiModelProperty(value="租客子订单号",required=true)
    @NotBlank(message="renterOrderNo不能为空")
    private String renterOrderNo;
    
    @AutoDocProperty("姓名")
    private String realName;
    @AutoDocProperty("手机号")
    private String telephone;
    @AutoDocProperty("身份证号")
    private String IDcard;
    @AutoDocProperty("驾驶证号")
    private String driverLicenseNo;
    @AutoDocProperty("驾驶证级别")
    private String driverLevel;
    @AutoDocProperty("驾驶证有效起始时间")
    private String driverBeginTime;
    @AutoDocProperty("驾驶证有效终止时间")
    private String driverEndTime;
    @AutoDocProperty("附加驾驶员险费用")
    private String driverAmt;
}
