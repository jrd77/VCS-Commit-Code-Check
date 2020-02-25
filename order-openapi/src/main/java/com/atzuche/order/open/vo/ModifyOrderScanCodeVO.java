package com.atzuche.order.open.vo;

import org.hibernate.validator.constraints.NotBlank;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ModifyOrderScanCodeVO {
	
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
	@NotBlank(message="ownerMemNo不能为空")
	@AutoDocProperty(value="ownerMemNo,必填，",required=true)
	private String ownerMemNo;
	
	@AutoDocProperty("【5.6新增】结算方式,1-按照订单结束时间结算,2-按照实际还车时间结算")
    @NotBlank(message = "结算方式不能为空")
    private String settleFlag;
	
	@AutoDocProperty(value="还车时间,格式 yyyyMMddHHmmss")
	private String revertTime;
	
}
