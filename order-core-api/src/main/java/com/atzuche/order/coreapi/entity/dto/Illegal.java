package com.atzuche.order.coreapi.entity.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 违章查询实体对象
 * @author chuanhong.liu
 *
 */
public class Illegal implements Serializable {

	private static final long serialVersionUID = -8293971365865512638L;

	/** 订单编号 **/
	private String orderNo;
	
	/** 车辆编号 **/
	private String carNo;

	/** 车牌号 **/
	private String plateNum;
	
	/** 租客电话 **/
	private String renterPhone;
	
	/** 租客会员号 **/
	private String rentNo;
	/**
	 * 车主会员号
	 */
	private String ownerNo;
	/**
	 * 车主手机号
	 */
	private String ownerPhone;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}

	public String getPlateNum() {
		return plateNum;
	}

	public void setPlateNum(String plateNum) {
		this.plateNum = plateNum;
	}

	public String getRenterPhone() {
		return renterPhone;
	}

	public void setRenterPhone(String renterPhone) {
		this.renterPhone = renterPhone;
	}

	public String getRentNo() {
		return rentNo;
	}

	public void setRentNo(String rentNo) {
		this.rentNo = rentNo;
	}

	public String getOwnerNo() {
		return ownerNo;
	}

	public void setOwnerNo(String ownerNo) {
		this.ownerNo = ownerNo;
	}

	public String getOwnerPhone() {
		return ownerPhone;
	}

	public void setOwnerPhone(String ownerPhone) {
		this.ownerPhone = ownerPhone;
	}

	@Override
	public String toString() {
		return "Illegal{" +
				"orderNo='" + orderNo + '\'' +
				", carNo='" + carNo + '\'' +
				", plateNum='" + plateNum + '\'' +
				", renterPhone='" + renterPhone + '\'' +
				", rentNo='" + rentNo + '\'' +
				", ownerNo='" + ownerNo + '\'' +
				", ownerPhone='" + ownerPhone + '\'' +
				'}';
	}
}
