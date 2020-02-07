package com.atzuche.order.admin.vo.resp.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;


@ToString
public class ModificationOrderResponseVO implements Serializable{
	@AutoDocProperty(value="子订单编号")
	private String renterOrderNo;

	@AutoDocProperty(value="下单/申请修改时间")
	private String modificationTime;

	@AutoDocProperty(value="修改来源:后台管理，租客，车主")
	private String source;

	@AutoDocProperty(value="修改人")
	private String modificationUser;

	@AutoDocProperty(value="修改原因")
	private String modificationReason;

	@AutoDocProperty(value="起租时间")
	private String rentTime;

	@AutoDocProperty(value="结束时间")
	private String revertTime;
	
	@AutoDocProperty(value="总租期")
	private String totalRentDay;
	
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

	@AutoDocProperty(value="配送费(含超运能)")
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

	@AutoDocProperty(value="车主操作状态:未处理，已同意，已拒绝")
	private String operatorStatus;

	public String getRenterOrderNo() {
		return renterOrderNo;
	}

	public void setRenterOrderNo(String renterOrderNo) {
		this.renterOrderNo = renterOrderNo;
	}

	public String getModificationTime() {
		return modificationTime;
	}

	public void setModificationTime(String modificationTime) {
		this.modificationTime = modificationTime;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getModificationUser() {
		return modificationUser;
	}

	public void setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
	}

	public String getModificationReason() {
		return modificationReason;
	}

	public void setModificationReason(String modificationReason) {
		this.modificationReason = modificationReason;
	}

	public String getRentTime() {
		return rentTime;
	}

	public void setRentTime(String rentTime) {
		this.rentTime = rentTime;
	}

	public String getRevertTime() {
		return revertTime;
	}

	public void setRevertTime(String revertTime) {
		this.revertTime = revertTime;
	}

	public String getTotalRentDay() {
		return totalRentDay;
	}

	public void setTotalRentDay(String totalRentDay) {
		this.totalRentDay = totalRentDay;
	}

	public String getRentAmount() {
		return rentAmount;
	}

	public void setRentAmount(String rentAmount) {
		this.rentAmount = rentAmount;
	}

	public String getInsuranceAmount() {
		return insuranceAmount;
	}

	public void setInsuranceAmount(String insuranceAmount) {
		this.insuranceAmount = insuranceAmount;
	}

	public String getSupperInsuranceAmount() {
		return supperInsuranceAmount;
	}

	public void setSupperInsuranceAmount(String supperInsuranceAmount) {
		this.supperInsuranceAmount = supperInsuranceAmount;
	}

	public String getAdditionalDriverInsuranceAmount() {
		return additionalDriverInsuranceAmount;
	}

	public void setAdditionalDriverInsuranceAmount(String additionalDriverInsuranceAmount) {
		this.additionalDriverInsuranceAmount = additionalDriverInsuranceAmount;
	}

	public String getServiceCharge() {
		return serviceCharge;
	}

	public void setServiceCharge(String serviceCharge) {
		this.serviceCharge = serviceCharge;
	}

	public String getCarServiceInformation() {
		return carServiceInformation;
	}

	public void setCarServiceInformation(String carServiceInformation) {
		this.carServiceInformation = carServiceInformation;
	}

	public String getGetAddress() {
		return getAddress;
	}

	public void setGetAddress(String getAddress) {
		this.getAddress = getAddress;
	}

	public String getReturnAddress() {
		return returnAddress;
	}

	public void setReturnAddress(String returnAddress) {
		this.returnAddress = returnAddress;
	}

	public String getCarServiceFee() {
		return carServiceFee;
	}

	public void setCarServiceFee(String carServiceFee) {
		this.carServiceFee = carServiceFee;
	}

	public String getDeductionAmount() {
		return deductionAmount;
	}

	public void setDeductionAmount(String deductionAmount) {
		this.deductionAmount = deductionAmount;
	}

	public String getVehicleDeposit() {
		return vehicleDeposit;
	}

	public void setVehicleDeposit(String vehicleDeposit) {
		this.vehicleDeposit = vehicleDeposit;
	}

	public String getViolationDeposit() {
		return violationDeposit;
	}

	public void setViolationDeposit(String violationDeposit) {
		this.violationDeposit = violationDeposit;
	}

	public String getCarServiceFine() {
		return carServiceFine;
	}

	public void setCarServiceFine(String carServiceFine) {
		this.carServiceFine = carServiceFine;
	}

	public String getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(String paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getOperatorStatus() {
		return operatorStatus;
	}

	public void setOperatorStatus(String operatorStatus) {
		this.operatorStatus = operatorStatus;
	}
	
	

}
