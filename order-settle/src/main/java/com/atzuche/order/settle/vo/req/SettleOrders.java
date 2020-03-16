package com.atzuche.order.settle.vo.req;

import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import lombok.Data;

import java.util.List;

/**
 * 订单结算对象
 */
//@Data
public class SettleOrders {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 租客订单号
     */
    private String renterOrderNo;
    /**
     * 车主订单号
     */
    private String ownerOrderNo;

    /**
     * 租客会员号
     */
    private String renterMemNo;

    /**
     * 车主会员号
     */
    private String ownerMemNo;

    /**
     * 租车费用
     */
//    private int renterOrderCost;

    /**
     * 租客费用明细
     */
    private RentCosts rentCosts;

    /**
     * 车主费用明细
     */
    private OwnerCosts ownerCosts;

    /**
     *  租客订单信息
     */
    RenterOrderEntity renterOrder;
    /**
     * 车主订单信息
     */
    OwnerOrderEntity ownerOrder;
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getRenterOrderNo() {
		return renterOrderNo;
	}
	public void setRenterOrderNo(String renterOrderNo) {
		this.renterOrderNo = renterOrderNo;
	}
	public String getOwnerOrderNo() {
		return ownerOrderNo;
	}
	public void setOwnerOrderNo(String ownerOrderNo) {
		this.ownerOrderNo = ownerOrderNo;
	}
	public String getRenterMemNo() {
		return renterMemNo;
	}
	public void setRenterMemNo(String renterMemNo) {
		this.renterMemNo = renterMemNo;
	}
	public String getOwnerMemNo() {
		return ownerMemNo;
	}
	public void setOwnerMemNo(String ownerMemNo) {
		this.ownerMemNo = ownerMemNo;
	}
//	public int getRenterOrderCost() {
//		return renterOrderCost;
//	}
//	public void setRenterOrderCost(int renterOrderCost) {
//		this.renterOrderCost = renterOrderCost;
//	}
	public RentCosts getRentCosts() {
		return rentCosts;
	}
	public void setRentCosts(RentCosts rentCosts) {
		this.rentCosts = rentCosts;
	}
	public OwnerCosts getOwnerCosts() {
		return ownerCosts;
	}
	public void setOwnerCosts(OwnerCosts ownerCosts) {
		this.ownerCosts = ownerCosts;
	}
	public RenterOrderEntity getRenterOrder() {
		return renterOrder;
	}
	public void setRenterOrder(RenterOrderEntity renterOrder) {
		this.renterOrder = renterOrder;
	}
	public OwnerOrderEntity getOwnerOrder() {
		return ownerOrder;
	}
	public void setOwnerOrder(OwnerOrderEntity ownerOrder) {
		this.ownerOrder = ownerOrder;
	}
    
    
}
