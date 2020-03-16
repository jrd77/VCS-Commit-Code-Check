package com.atzuche.order.settle.vo.req;

import com.atzuche.order.delivery.vo.delivery.rep.OwnerGetAndReturnCarDTO;
import com.atzuche.order.delivery.vo.delivery.rep.RenterGetAndReturnCarDTO;
import com.atzuche.order.ownercost.entity.*;
import com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.autoyol.platformcost.model.FeeResult;

import lombok.Data;

import java.util.List;

/**
 * 车主费用信息
 * @author haibao.yan
 */
//@Data
public class OwnerCosts {

    /**
     * 车主端代管车服务费
     */
    private OwnerOrderPurchaseDetailEntity proxyExpense;
    /**
     * 车主端平台服务费
     */
    private OwnerOrderPurchaseDetailEntity serviceExpense;
    /**
     * 获取车主补贴明细列表
     */
    private List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetail;
    /**
     * 获取车主费用列表
     */
    private List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetail;
    /**
     * 获取车主增值服务费用列表
     */
    private List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetail;

    /**
     * 获取gps服务费
     */
    private List<OwnerOrderPurchaseDetailEntity> gpsCost;

    /**
     * 获取车主油费
     */
    private OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO;
    /**
     * 车主结算分离，需要计算油费差价，需要将租客的也计算出来。
     * 需要平账，去掉。
     */
//    private RenterGetAndReturnCarDTO renterGetAndReturnCarDTO;
    /**
     * 管理后台补贴
     */
    List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails;
    /**
     * 全局的车主订单罚金明细
     */
    List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailEntitys;

    /**
     * 车主订单罚金明细
     */
    List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatails;

    /**
     * 后台管理操作费用表（无条件补贴）
     */
    List<OrderConsoleCostDetailEntity> orderConsoleCostDetailEntity;

    /**
     * 平台加油服务费用
     */
    private int ownerPlatFormOilService;
    
    ///add huangjing  来源：SettleOrdersDefinition类字段。 200214
    private int ownerCostAmtFinal;
    //车主会员号
    private String ownerNo;
    
    
//    public RenterGetAndReturnCarDTO getRenterGetAndReturnCarDTO() {
//		return renterGetAndReturnCarDTO;
//	}
//
//	public void setRenterGetAndReturnCarDTO(RenterGetAndReturnCarDTO renterGetAndReturnCarDTO) {
//		this.renterGetAndReturnCarDTO = renterGetAndReturnCarDTO;
//	}

	/**
     * 交接车-获取超里程费用（车主端依托租客的参数来计算。）
     */
    private FeeResult mileageAmt;
    //车辆类型
    private Integer carOwnerType;
    
    
	public FeeResult getMileageAmt() {
		return mileageAmt;
	}

	public void setMileageAmt(FeeResult mileageAmt) {
		this.mileageAmt = mileageAmt;
	}

	public Integer getCarOwnerType() {
		return carOwnerType;
	}

	public void setCarOwnerType(Integer carOwnerType) {
		this.carOwnerType = carOwnerType;
	}


	public String getOwnerNo() {
		return ownerNo;
	}

	public void setOwnerNo(String ownerNo) {
		this.ownerNo = ownerNo;
	}

	public OwnerOrderPurchaseDetailEntity getProxyExpense() {
		return proxyExpense;
	}

	public void setProxyExpense(OwnerOrderPurchaseDetailEntity proxyExpense) {
		this.proxyExpense = proxyExpense;
	}

	public OwnerOrderPurchaseDetailEntity getServiceExpense() {
		return serviceExpense;
	}

	public void setServiceExpense(OwnerOrderPurchaseDetailEntity serviceExpense) {
		this.serviceExpense = serviceExpense;
	}

	public List<OwnerOrderSubsidyDetailEntity> getOwnerOrderSubsidyDetail() {
		return ownerOrderSubsidyDetail;
	}

	public void setOwnerOrderSubsidyDetail(List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetail) {
		this.ownerOrderSubsidyDetail = ownerOrderSubsidyDetail;
	}

	public List<OwnerOrderPurchaseDetailEntity> getOwnerOrderPurchaseDetail() {
		return ownerOrderPurchaseDetail;
	}

	public void setOwnerOrderPurchaseDetail(List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetail) {
		this.ownerOrderPurchaseDetail = ownerOrderPurchaseDetail;
	}

	public List<OwnerOrderIncrementDetailEntity> getOwnerOrderIncrementDetail() {
		return ownerOrderIncrementDetail;
	}

	public void setOwnerOrderIncrementDetail(List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetail) {
		this.ownerOrderIncrementDetail = ownerOrderIncrementDetail;
	}

	public List<OwnerOrderPurchaseDetailEntity> getGpsCost() {
		return gpsCost;
	}

	public void setGpsCost(List<OwnerOrderPurchaseDetailEntity> gpsCost) {
		this.gpsCost = gpsCost;
	}

	public OwnerGetAndReturnCarDTO getOwnerGetAndReturnCarDTO() {
		return ownerGetAndReturnCarDTO;
	}

	public void setOwnerGetAndReturnCarDTO(OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO) {
		this.ownerGetAndReturnCarDTO = ownerGetAndReturnCarDTO;
	}

	public List<OrderConsoleSubsidyDetailEntity> getOrderConsoleSubsidyDetails() {
		return orderConsoleSubsidyDetails;
	}

	public void setOrderConsoleSubsidyDetails(List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails) {
		this.orderConsoleSubsidyDetails = orderConsoleSubsidyDetails;
	}

	public List<ConsoleOwnerOrderFineDeatailEntity> getConsoleOwnerOrderFineDeatailEntitys() {
		return consoleOwnerOrderFineDeatailEntitys;
	}

	public void setConsoleOwnerOrderFineDeatailEntitys(
			List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailEntitys) {
		this.consoleOwnerOrderFineDeatailEntitys = consoleOwnerOrderFineDeatailEntitys;
	}

	public List<OwnerOrderFineDeatailEntity> getOwnerOrderFineDeatails() {
		return ownerOrderFineDeatails;
	}

	public void setOwnerOrderFineDeatails(List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatails) {
		this.ownerOrderFineDeatails = ownerOrderFineDeatails;
	}

	public List<OrderConsoleCostDetailEntity> getOrderConsoleCostDetailEntity() {
		return orderConsoleCostDetailEntity;
	}

	public void setOrderConsoleCostDetailEntity(List<OrderConsoleCostDetailEntity> orderConsoleCostDetailEntity) {
		this.orderConsoleCostDetailEntity = orderConsoleCostDetailEntity;
	}

	public int getOwnerPlatFormOilService() {
		return ownerPlatFormOilService;
	}

	public void setOwnerPlatFormOilService(int ownerPlatFormOilService) {
		this.ownerPlatFormOilService = ownerPlatFormOilService;
	}

	public int getOwnerCostAmtFinal() {
		return ownerCostAmtFinal;
	}

	public void setOwnerCostAmtFinal(int ownerCostAmtFinal) {
		this.ownerCostAmtFinal = ownerCostAmtFinal;
	}
    
    
}
