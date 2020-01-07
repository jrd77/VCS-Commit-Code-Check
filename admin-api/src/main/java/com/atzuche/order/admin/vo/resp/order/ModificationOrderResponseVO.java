package com.atzuche.order.admin.vo.resp.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;


@Data
@ToString
public class ModificationOrderResponseVO implements Serializable{
	@AutoDocProperty(value="子订单编号")
	private String renterOrderNo;

	@AutoDocProperty(value="下单/申请修改时间")
	private String modificationTime;

	@AutoDocProperty(value="修改来源")
	private String source;

	@AutoDocProperty(value="修改人")
	private String modificationUser;

	@AutoDocProperty(value="修改原因")
	private String modificationReason;

	@AutoDocProperty(value="起租时间")
	private String rentTime;

	@AutoDocProperty(value="结束时间")
	private String revertTime;

	@AutoDocProperty(value="租金")
	private String rentAmount;

	@AutoDocProperty(value="平台保障费")
	private String insuranceAmount;

	@AutoDocProperty(value="全面保障服务费")
	private String supperInsuranceAmount;

	@AutoDocProperty(value="附加驾驶员险")
	private String additionalDriverInsuranceAmount;

	@AutoDocProperty(value="手续费")
	private String serviceCharge;

	@AutoDocProperty(value="取还车服务")
	private String carServiceInformation;

	@AutoDocProperty(value="取车地址")
	private String getAddress;

	@AutoDocProperty(value="还车地址")
	private String returnAddress;

	@AutoDocProperty(value="配送费")
	private String carServiceFee;

	@AutoDocProperty(value="优惠抵扣")
	private String deductionAmount;

	@AutoDocProperty(value="车辆押金")
	private String vehicleDeposit;

	@AutoDocProperty(value="违章押金")
	private String violationDeposit;

	@AutoDocProperty(value="取还车服务违约金")
	private String carServiceFine;

	@AutoDocProperty(value="需补付金额")
	private String paymentAmount;

	@AutoDocProperty(value="车主操作状态")
	private String operatorStatus;

}
