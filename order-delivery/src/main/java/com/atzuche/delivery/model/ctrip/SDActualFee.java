package com.atzuche.delivery.model.ctrip;

import com.alibaba.fastjson.annotation.JSONField;

public class SDActualFee {

	private int Type; // 费用类型
	private float Quantity; // 数量
	private double UnitPrice; // 单价
	private double TotalPrice; // 总价
	private String Name; // 费用名称
	private String Description; // 费用描述
	private boolean IsOffline; // 是否线下购买
	@JSONField(name = "Type")
	public int getType() {
		return Type;
	}
	public void setType(int type) {
		Type = type;
	}
	@JSONField(name = "Quantity")
	public float getQuantity() {
		return Quantity;
	}
	public void setQuantity(float quantity) {
		Quantity = quantity;
	}
	@JSONField(name = "UnitPrice")
	public double getUnitPrice() {
		return UnitPrice;
	}
	public void setUnitPrice(double unitPrice) {
		UnitPrice = unitPrice;
	}
	@JSONField(name = "TotalPrice")
	public double getTotalPrice() {
		return TotalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		TotalPrice = totalPrice;
	}
	@JSONField(name = "Name")
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	@JSONField(name = "Description")
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	@JSONField(name = "IsOffline")
	public boolean getIsOffline() {
		return IsOffline;
	}
	public void setIsOffline(boolean isOffline) {
		IsOffline = isOffline;
	}
}
