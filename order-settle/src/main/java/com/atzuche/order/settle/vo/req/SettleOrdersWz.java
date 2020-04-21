package com.atzuche.order.settle.vo.req;

import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;

/**
 * 订单违章结算对象
 */
//@Data
public class SettleOrdersWz {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 租客订单号
     */
    private String renterOrderNo;
    /**
     * 租客会员号
     */
    private String renterMemNo;

    /**
     * 违章费用
     */
    private int renterOrderCostWz;

    /**
     * 租客违章费用明细
     */
    private RentCostsWz rentCostsWz;

    /**
     *  租客订单信息
     */
    RenterOrderEntity renterOrder;
    
    ///预留字段，违章结算跟车主无关。
    /**
	* 车主订单号
	*/
	private String ownerOrderNo;
	/**
	* 车主会员号
	*/
	private String ownerMemNo;
	/**
	* 车主订单信息
	*/
	OwnerOrderEntity ownerOrder;
	
	/**
	 * 违章押金抵扣老系统欠款金额
	 */
	private Integer totalWzDebtAmt;
	
	/**
	 * 违章押金是否虚拟支付
	 */
	private Boolean wzCostVirtualFlag;


    /**
     * 是否企业级用户订单
     */
	private Boolean isEnterpriseUserOrder;

    /**
     * 应该收取的违章费用(违章费用 + 未支付补付记录金额 - 无需支付的补付金额)
     */
	private int shouldTakeWzCost;
	
	
	public String getOwnerOrderNo() {
		return ownerOrderNo;
	}

	public void setOwnerOrderNo(String ownerOrderNo) {
		this.ownerOrderNo = ownerOrderNo;
	}

	public String getOwnerMemNo() {
		return ownerMemNo;
	}

	public void setOwnerMemNo(String ownerMemNo) {
		this.ownerMemNo = ownerMemNo;
	}

	public OwnerOrderEntity getOwnerOrder() {
		return ownerOrder;
	}

	public void setOwnerOrder(OwnerOrderEntity ownerOrder) {
		this.ownerOrder = ownerOrder;
	}

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

	public String getRenterMemNo() {
		return renterMemNo;
	}

	public void setRenterMemNo(String renterMemNo) {
		this.renterMemNo = renterMemNo;
	}

	public int getRenterOrderCostWz() {
		return renterOrderCostWz;
	}

	public void setRenterOrderCostWz(int renterOrderCostWz) {
		this.renterOrderCostWz = renterOrderCostWz;
	}

	public RentCostsWz getRentCostsWz() {
		return rentCostsWz;
	}

	public void setRentCostsWz(RentCostsWz rentCostsWz) {
		this.rentCostsWz = rentCostsWz;
	}

	public RenterOrderEntity getRenterOrder() {
		return renterOrder;
	}

	public void setRenterOrder(RenterOrderEntity renterOrder) {
		this.renterOrder = renterOrder;
	}

	public Integer getTotalWzDebtAmt() {
		return totalWzDebtAmt;
	}

	public void setTotalWzDebtAmt(Integer totalWzDebtAmt) {
		this.totalWzDebtAmt = totalWzDebtAmt;
	}

	public Boolean getWzCostVirtualFlag() {
		return wzCostVirtualFlag;
	}

	public void setWzCostVirtualFlag(Boolean wzCostVirtualFlag) {
		this.wzCostVirtualFlag = wzCostVirtualFlag;
	}

    public Boolean getEnterpriseUserOrder() {
        return isEnterpriseUserOrder;
    }

    public void setEnterpriseUserOrder(Boolean enterpriseUserOrder) {
        isEnterpriseUserOrder = enterpriseUserOrder;
    }

    public int getShouldTakeWzCost() {
        return shouldTakeWzCost;
    }

    public void setShouldTakeWzCost(int shouldTakeWzCost) {
        this.shouldTakeWzCost = shouldTakeWzCost;
    }
}
