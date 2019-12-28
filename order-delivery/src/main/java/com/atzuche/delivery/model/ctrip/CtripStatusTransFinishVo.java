package com.atzuche.delivery.model.ctrip;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class CtripStatusTransFinishVo {

	private String VendorCode;
	private String OperateSerialNumber; // 操作流水号
	private long CtripOrderId; // 携程订单id
	private int VendorOrderStatus; // 回调状态
	private double ActualAmount; // 实际用车金额
	private String ActualPickupTime; // 实际取车时间
	private String ActualReturnTime; // 实际还车时间
	private String[] BackFeeList;
	private List<SDActualFee> SDActualFeeList;
	private double PeccancyDepositAmount; // 违章押金
	private String PeccancyDepositReturnTime;
	private double ReturnPeccancyDepositAmount;
	private double ViolateAmount;
	private double DepositAmount;
	@JSONField(name = "VendorCode")
	public String getVendorCode() {
		return VendorCode;
	}
	public void setVendorCode(String vendorCode) {
		VendorCode = vendorCode;
	}
	@JSONField(name = "OperateSerialNumber")
	public String getOperateSerialNumber() {
		return OperateSerialNumber;
	}
	public void setOperateSerialNumber(String operateSerialNumber) {
		OperateSerialNumber = operateSerialNumber;
	}
	@JSONField(name = "CtripOrderId")
	public long getCtripOrderId() {
		return CtripOrderId;
	}
	public void setCtripOrderId(long ctripOrderId) {
		CtripOrderId = ctripOrderId;
	}
	@JSONField(name = "VendorOrderStatus")
	public int getVendorOrderStatus() {
		return VendorOrderStatus;
	}
	public void setVendorOrderStatus(int vendorOrderStatus) {
		VendorOrderStatus = vendorOrderStatus;
	}
	@JSONField(name = "ActualAmount")
	public double getActualAmount() {
		return ActualAmount;
	}
	public void setActualAmount(double actualAmount) {
		ActualAmount = actualAmount;
	}
	@JSONField(name = "ActualPickupTime")
	public String getActualPickupTime() {
		return ActualPickupTime;
	}
	public void setActualPickupTime(String actualPickupTime) {
		ActualPickupTime = actualPickupTime;
	}
	@JSONField(name = "ActualReturnTime")
	public String getActualReturnTime() {
		return ActualReturnTime;
	}
	public void setActualReturnTime(String actualReturnTime) {
		ActualReturnTime = actualReturnTime;
	}
	@JSONField(name = "BackFeeList")
	public String[] getBackFeeList() {
		return BackFeeList;
	}
	public void setBackFeeList(String[] backFeeList) {
		BackFeeList = backFeeList;
	}
	@JSONField(name = "SDActualFeeList")
	public List<SDActualFee> getSDActualFeeList() {
		return SDActualFeeList;
	}
	public void setSDActualFeeList(List<SDActualFee> sDActualFeeList) {
		SDActualFeeList = sDActualFeeList;
	}
	@JSONField(name = "PeccancyDepositAmount")
	public double getPeccancyDepositAmount() {
		return PeccancyDepositAmount;
	}
	public void setPeccancyDepositAmount(double peccancyDepositAmount) {
		PeccancyDepositAmount = peccancyDepositAmount;
	}
	@JSONField(name = "PeccancyDepositReturnTime")
	public String getPeccancyDepositReturnTime() {
		return PeccancyDepositReturnTime;
	}
	public void setPeccancyDepositReturnTime(String peccancyDepositReturnTime) {
		PeccancyDepositReturnTime = peccancyDepositReturnTime;
	}
	@JSONField(name = "ReturnPeccancyDepositAmount")
	public double getReturnPeccancyDepositAmount() {
		return ReturnPeccancyDepositAmount;
	}
	public void setReturnPeccancyDepositAmount(double returnPeccancyDepositAmount) {
		ReturnPeccancyDepositAmount = returnPeccancyDepositAmount;
	}
	@JSONField(name = "ViolateAmount")
	public double getViolateAmount() {
		return ViolateAmount;
	}
	public void setViolateAmount(double violateAmount) {
		ViolateAmount = violateAmount;
	}
	@JSONField(name = "DepositAmount")
	public double getDepositAmount() {
		return DepositAmount;
	}
	public void setDepositAmount(double depositAmount) {
		DepositAmount = depositAmount;
	}
	
}
