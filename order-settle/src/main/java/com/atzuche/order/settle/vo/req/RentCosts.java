package com.atzuche.order.settle.vo.req;

import com.atzuche.order.delivery.vo.delivery.rep.RenterGetAndReturnCarDTO;
import com.atzuche.order.rentercost.entity.*;
import com.autoyol.platformcost.model.FeeResult;
import lombok.Data;

import java.util.List;

/**
 * 租客费用信息
 */
//@Data
public class RentCosts {
    /**
     * 查询租车费用
     */
    private List<RenterOrderCostDetailEntity> renterOrderCostDetails;

    /**
     * 交接车-油费
     */
    private RenterGetAndReturnCarDTO oilAmt;

    /**
     * 交接车-获取超里程费用
     */
    private FeeResult mileageAmt;

    /**
     * 补贴
     */
    private List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetails;

    /**
     * 租客罚金
     */
    private List<RenterOrderFineDeatailEntity> renterOrderFineDeatails;

    /**
     * 管理后台补贴
     */
    private List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails;

    /**
     * 获取全局的租客订单罚金明细
     */
    private List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatails;

    /**
     * 后台管理操作费用表（无条件补贴）
     */
    List<OrderConsoleCostDetailEntity> orderConsoleCostDetailEntity;

    /**
              * 租客最终收益金额（应收）
     */
    private int renterCostAmtFinal;
    //租客会员号
    private String renterNo;
    
    
	public String getRenterNo() {
		return renterNo;
	}

	public void setRenterNo(String renterNo) {
		this.renterNo = renterNo;
	}

	public int getRenterCostAmtFinal() {
		return renterCostAmtFinal;
	}

	public void setRenterCostAmtFinal(int renterCostAmtFinal) {
		this.renterCostAmtFinal = renterCostAmtFinal;
	}

	public List<RenterOrderCostDetailEntity> getRenterOrderCostDetails() {
		return renterOrderCostDetails;
	}

	public void setRenterOrderCostDetails(List<RenterOrderCostDetailEntity> renterOrderCostDetails) {
		this.renterOrderCostDetails = renterOrderCostDetails;
	}

	public RenterGetAndReturnCarDTO getOilAmt() {
		return oilAmt;
	}

	public void setOilAmt(RenterGetAndReturnCarDTO oilAmt) {
		this.oilAmt = oilAmt;
	}

	public FeeResult getMileageAmt() {
		return mileageAmt;
	}

	public void setMileageAmt(FeeResult mileageAmt) {
		this.mileageAmt = mileageAmt;
	}

	public List<RenterOrderSubsidyDetailEntity> getRenterOrderSubsidyDetails() {
		return renterOrderSubsidyDetails;
	}

	public void setRenterOrderSubsidyDetails(List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetails) {
		this.renterOrderSubsidyDetails = renterOrderSubsidyDetails;
	}

	public List<RenterOrderFineDeatailEntity> getRenterOrderFineDeatails() {
		return renterOrderFineDeatails;
	}

	public void setRenterOrderFineDeatails(List<RenterOrderFineDeatailEntity> renterOrderFineDeatails) {
		this.renterOrderFineDeatails = renterOrderFineDeatails;
	}

	public List<OrderConsoleSubsidyDetailEntity> getOrderConsoleSubsidyDetails() {
		return orderConsoleSubsidyDetails;
	}

	public void setOrderConsoleSubsidyDetails(List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails) {
		this.orderConsoleSubsidyDetails = orderConsoleSubsidyDetails;
	}

	public List<ConsoleRenterOrderFineDeatailEntity> getConsoleRenterOrderFineDeatails() {
		return consoleRenterOrderFineDeatails;
	}

	public void setConsoleRenterOrderFineDeatails(
			List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatails) {
		this.consoleRenterOrderFineDeatails = consoleRenterOrderFineDeatails;
	}

	public List<OrderConsoleCostDetailEntity> getOrderConsoleCostDetailEntity() {
		return orderConsoleCostDetailEntity;
	}

	public void setOrderConsoleCostDetailEntity(List<OrderConsoleCostDetailEntity> orderConsoleCostDetailEntity) {
		this.orderConsoleCostDetailEntity = orderConsoleCostDetailEntity;
	}
    
    
}
