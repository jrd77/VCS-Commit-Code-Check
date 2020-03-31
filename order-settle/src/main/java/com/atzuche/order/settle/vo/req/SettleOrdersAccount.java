package com.atzuche.order.settle.vo.req;

import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import lombok.Data;


/**
 * 计算费用统计
 */
//@Data
public class SettleOrdersAccount {



    /** ***********************************************************************************车主端统计费用 统计费用 */

    /**
     * 车主收益金额
     */
    private int ownerCostAmtFinal;
    /**
     * 车主最终收益金额
     */
    private int ownerCostSurplusAmt;

    /**
     * 租客应付费用
     */
    private int rentCostAmtFinal;
    /**
     * 租客实付付费用 （在实付小于应付情况下 包含真实消费金额 + 押金抵扣租车费用部分 + 转移历史欠款冲正，用于计算部落库）
     * （实付大于应付时候 ，为真实消费金额）
     */
    private int rentCostPayAmt;
    /**
     * 当 租车费用 实付大于应付时候 有效
     * 剩余租车费用 即应退
     */
    private int rentCostSurplusAmt;

    /**
     * 租客实付 车辆押金
     */
    private int depositAmt;

    /**
     * 剩余 车辆押金
     */
    private int depositSurplusAmt;

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
     * 订单流转状态
     */
    private OrderStatusDTO orderStatusDTO;
    /**
     * 租车费用是否虚拟支付
     */
    private Boolean rentCostVirtualPayFlag;
    /**
     * 车辆押金是否虚拟支付
     */
    private Boolean rentDepositVirtualPayFlag;
    /**
	 * 违章押金是否虚拟支付
	 */
	private Boolean wzCostVirtualFlag;

	public int getOwnerCostAmtFinal() {
		return ownerCostAmtFinal;
	}


	public void setOwnerCostAmtFinal(int ownerCostAmtFinal) {
		this.ownerCostAmtFinal = ownerCostAmtFinal;
	}


	public int getOwnerCostSurplusAmt() {
		return ownerCostSurplusAmt;
	}


	public void setOwnerCostSurplusAmt(int ownerCostSurplusAmt) {
		this.ownerCostSurplusAmt = ownerCostSurplusAmt;
	}


	public int getRentCostAmtFinal() {
		return rentCostAmtFinal;
	}


	public void setRentCostAmtFinal(int rentCostAmtFinal) {
		this.rentCostAmtFinal = rentCostAmtFinal;
	}


	public int getRentCostPayAmt() {
		return rentCostPayAmt;
	}


	public void setRentCostPayAmt(int rentCostPayAmt) {
		this.rentCostPayAmt = rentCostPayAmt;
	}


	public int getRentCostSurplusAmt() {
		return rentCostSurplusAmt;
	}


	public void setRentCostSurplusAmt(int rentCostSurplusAmt) {
		this.rentCostSurplusAmt = rentCostSurplusAmt;
	}


	public int getDepositAmt() {
		return depositAmt;
	}


	public void setDepositAmt(int depositAmt) {
		this.depositAmt = depositAmt;
	}


	public int getDepositSurplusAmt() {
		return depositSurplusAmt;
	}


	public void setDepositSurplusAmt(int depositSurplusAmt) {
		this.depositSurplusAmt = depositSurplusAmt;
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


	public OrderStatusDTO getOrderStatusDTO() {
		return orderStatusDTO;
	}


	public void setOrderStatusDTO(OrderStatusDTO orderStatusDTO) {
		this.orderStatusDTO = orderStatusDTO;
	}


	public Boolean getRentCostVirtualPayFlag() {
		return rentCostVirtualPayFlag;
	}


	public void setRentCostVirtualPayFlag(Boolean rentCostVirtualPayFlag) {
		this.rentCostVirtualPayFlag = rentCostVirtualPayFlag;
	}


	public Boolean getRentDepositVirtualPayFlag() {
		return rentDepositVirtualPayFlag;
	}


	public void setRentDepositVirtualPayFlag(Boolean rentDepositVirtualPayFlag) {
		this.rentDepositVirtualPayFlag = rentDepositVirtualPayFlag;
	}


	public Boolean getWzCostVirtualFlag() {
		return wzCostVirtualFlag;
	}


	public void setWzCostVirtualFlag(Boolean wzCostVirtualFlag) {
		this.wzCostVirtualFlag = wzCostVirtualFlag;
	}

}
