package com.atzuche.order.commons.entity.orderDetailDto;
import com.atzuche.order.commons.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.commons.vo.OrderStopFreightInfo;
import com.atzuche.order.commons.vo.res.SectionDeliveryVO;

import lombok.Data;
import lombok.ToString;

import java.util.List;


@ToString
public class OrderDetailRespDTO {
    public OrderDTO order;
    public RenterOrderDTO renterOrder;
    public OwnerOrderDTO ownerOrder;
    public OrderStatusDTO orderStatus;
    public OrderSourceStatDTO orderSourceStat;
    public RenterGoodsDTO renterGoods;
    public OwnerGoodsDTO ownerGoods;
    public RenterOrderDeliveryDTO renterOrderDeliveryGet;
    public RenterOrderDeliveryDTO renterOrderDeliveryReturn;
    public RenterMemberDTO renterMember;
    public OwnerMemberDTO ownerMember;
    public RenterOrderCostDTO renterOrderCost;
    public RenterHandoverCarInfoDTO renterHandoverCarInfo;
    public OwnerHandoverCarInfoDTO ownerHandoverCarInfo;
    //public OrderCancelReasonDTO orderCancelReason;
    public List<OrderCancelReasonDTO> orderCancelReasons;
    public List<OwnerMemberRightDTO> ownerMemberRightList;
    public List<RenterMemberRightDTO> renterMemberRightList;
    public List<RenterOrderCostDetailDTO> renterOrderCostDetailList;
    public List<AccountOwnerIncomeDetailDTO> accountOwnerIncomeDetailList;
    public List<OwnerOrderPurchaseDetailDTO> ownerOrderPurchaseDetailList;
    public List<ConsoleOwnerOrderFineDeatailDTO> consoleOwnerOrderFineDetailList;
    public List<RenterAdditionalDriverDTO> renterAdditionalDriverList;
    public List<OwnerOrderFineDeatailDTO> ownerOrderFineDeatailList;
    public List<RenterOrderFineDeatailDTO> renterOrderFineDeatailList;
    public List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOS;
    public List<OwnerOrderSubsidyDetailDTO> ownerOrderSubsidyDetailDTOS;
    public OwnerOrderCostDTO ownerOrderCostDTO;
    public List<AccountOwnerIncomeExamineDTO> accountOwnerIncomeExamineDTOS;
    public List<RenterOrderChangeApplyDTO> renterOrderChangeApplyDTOS;
    public AccountRenterDepositDTO accountRenterDepositDTO;
    public AccountRenterWzDepositDTO accountRenterWzDepositDTO;
    public List<AccountRenterCostDetailDTO> accountRenterCostDetailDTO;
    public OrderRefundRecordDTO orderRefundRecordDTO;
    public RenterDeliveryAddrDTO renterDeliveryAddrDTO;

    //变更申请的租客子订单号（application_id）
    public String changeApplyRenterOrderNo;
    //是否在变更中
    public boolean isChangeApply = false;
    //是否自动拒绝
    public boolean isAutoRefuse = false;
    //租客修改订单申请表
    public RenterOrderChangeApplyDTO renterOrderChangeApplyDTO;
    //预计收益金额
    public Integer changeApplyPreIncomAmt;
    //车主预计收益
    public Integer ownerPreIncom;
    // 停运费信息
    public OrderStopFreightDTO orderStopFreightDTO;
    // 区间配送信息
    public SectionDeliveryVO sectionDelivery;
    //违章结算
    public WzQueryDayConfDTO wzQueryDayConfDTO;
	public OrderDTO getOrder() {
		return order;
	}
	public void setOrder(OrderDTO order) {
		this.order = order;
	}
	public RenterOrderDTO getRenterOrder() {
		return renterOrder;
	}
	public void setRenterOrder(RenterOrderDTO renterOrder) {
		this.renterOrder = renterOrder;
	}
	public OwnerOrderDTO getOwnerOrder() {
		return ownerOrder;
	}
	public void setOwnerOrder(OwnerOrderDTO ownerOrder) {
		this.ownerOrder = ownerOrder;
	}
	public OrderStatusDTO getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(OrderStatusDTO orderStatus) {
		this.orderStatus = orderStatus;
	}
	public OrderSourceStatDTO getOrderSourceStat() {
		return orderSourceStat;
	}
	public void setOrderSourceStat(OrderSourceStatDTO orderSourceStat) {
		this.orderSourceStat = orderSourceStat;
	}
	public RenterGoodsDTO getRenterGoods() {
		return renterGoods;
	}
	public void setRenterGoods(RenterGoodsDTO renterGoods) {
		this.renterGoods = renterGoods;
	}
	public OwnerGoodsDTO getOwnerGoods() {
		return ownerGoods;
	}
	public void setOwnerGoods(OwnerGoodsDTO ownerGoods) {
		this.ownerGoods = ownerGoods;
	}
	public RenterOrderDeliveryDTO getRenterOrderDeliveryGet() {
		return renterOrderDeliveryGet;
	}
	public void setRenterOrderDeliveryGet(RenterOrderDeliveryDTO renterOrderDeliveryGet) {
		this.renterOrderDeliveryGet = renterOrderDeliveryGet;
	}
	public RenterOrderDeliveryDTO getRenterOrderDeliveryReturn() {
		return renterOrderDeliveryReturn;
	}
	public void setRenterOrderDeliveryReturn(RenterOrderDeliveryDTO renterOrderDeliveryReturn) {
		this.renterOrderDeliveryReturn = renterOrderDeliveryReturn;
	}
	public RenterMemberDTO getRenterMember() {
		return renterMember;
	}
	public void setRenterMember(RenterMemberDTO renterMember) {
		this.renterMember = renterMember;
	}
	public OwnerMemberDTO getOwnerMember() {
		return ownerMember;
	}
	public void setOwnerMember(OwnerMemberDTO ownerMember) {
		this.ownerMember = ownerMember;
	}
	public RenterOrderCostDTO getRenterOrderCost() {
		return renterOrderCost;
	}
	public void setRenterOrderCost(RenterOrderCostDTO renterOrderCost) {
		this.renterOrderCost = renterOrderCost;
	}
	public RenterHandoverCarInfoDTO getRenterHandoverCarInfo() {
		return renterHandoverCarInfo;
	}
	public void setRenterHandoverCarInfo(RenterHandoverCarInfoDTO renterHandoverCarInfo) {
		this.renterHandoverCarInfo = renterHandoverCarInfo;
	}
	public OwnerHandoverCarInfoDTO getOwnerHandoverCarInfo() {
		return ownerHandoverCarInfo;
	}
	public void setOwnerHandoverCarInfo(OwnerHandoverCarInfoDTO ownerHandoverCarInfo) {
		this.ownerHandoverCarInfo = ownerHandoverCarInfo;
	}
	public List<OrderCancelReasonDTO> getOrderCancelReasons() {
		return orderCancelReasons;
	}
	public void setOrderCancelReasons(List<OrderCancelReasonDTO> orderCancelReasons) {
		this.orderCancelReasons = orderCancelReasons;
	}
	public List<OwnerMemberRightDTO> getOwnerMemberRightList() {
		return ownerMemberRightList;
	}
	public void setOwnerMemberRightList(List<OwnerMemberRightDTO> ownerMemberRightList) {
		this.ownerMemberRightList = ownerMemberRightList;
	}
	public List<RenterMemberRightDTO> getRenterMemberRightList() {
		return renterMemberRightList;
	}
	public void setRenterMemberRightList(List<RenterMemberRightDTO> renterMemberRightList) {
		this.renterMemberRightList = renterMemberRightList;
	}
	public List<RenterOrderCostDetailDTO> getRenterOrderCostDetailList() {
		return renterOrderCostDetailList;
	}
	public void setRenterOrderCostDetailList(List<RenterOrderCostDetailDTO> renterOrderCostDetailList) {
		this.renterOrderCostDetailList = renterOrderCostDetailList;
	}
	public List<AccountOwnerIncomeDetailDTO> getAccountOwnerIncomeDetailList() {
		return accountOwnerIncomeDetailList;
	}
	public void setAccountOwnerIncomeDetailList(List<AccountOwnerIncomeDetailDTO> accountOwnerIncomeDetailList) {
		this.accountOwnerIncomeDetailList = accountOwnerIncomeDetailList;
	}
	public List<OwnerOrderPurchaseDetailDTO> getOwnerOrderPurchaseDetailList() {
		return ownerOrderPurchaseDetailList;
	}
	public void setOwnerOrderPurchaseDetailList(List<OwnerOrderPurchaseDetailDTO> ownerOrderPurchaseDetailList) {
		this.ownerOrderPurchaseDetailList = ownerOrderPurchaseDetailList;
	}
	public List<ConsoleOwnerOrderFineDeatailDTO> getConsoleOwnerOrderFineDetailList() {
		return consoleOwnerOrderFineDetailList;
	}
	public void setConsoleOwnerOrderFineDetailList(List<ConsoleOwnerOrderFineDeatailDTO> consoleOwnerOrderFineDetailList) {
		this.consoleOwnerOrderFineDetailList = consoleOwnerOrderFineDetailList;
	}
	public List<RenterAdditionalDriverDTO> getRenterAdditionalDriverList() {
		return renterAdditionalDriverList;
	}
	public void setRenterAdditionalDriverList(List<RenterAdditionalDriverDTO> renterAdditionalDriverList) {
		this.renterAdditionalDriverList = renterAdditionalDriverList;
	}
	public List<OwnerOrderFineDeatailDTO> getOwnerOrderFineDeatailList() {
		return ownerOrderFineDeatailList;
	}
	public void setOwnerOrderFineDeatailList(List<OwnerOrderFineDeatailDTO> ownerOrderFineDeatailList) {
		this.ownerOrderFineDeatailList = ownerOrderFineDeatailList;
	}
	public List<RenterOrderFineDeatailDTO> getRenterOrderFineDeatailList() {
		return renterOrderFineDeatailList;
	}
	public void setRenterOrderFineDeatailList(List<RenterOrderFineDeatailDTO> renterOrderFineDeatailList) {
		this.renterOrderFineDeatailList = renterOrderFineDeatailList;
	}
	public List<RenterOrderSubsidyDetailDTO> getRenterOrderSubsidyDetailDTOS() {
		return renterOrderSubsidyDetailDTOS;
	}
	public void setRenterOrderSubsidyDetailDTOS(List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOS) {
		this.renterOrderSubsidyDetailDTOS = renterOrderSubsidyDetailDTOS;
	}
	public List<OwnerOrderSubsidyDetailDTO> getOwnerOrderSubsidyDetailDTOS() {
		return ownerOrderSubsidyDetailDTOS;
	}
	public void setOwnerOrderSubsidyDetailDTOS(List<OwnerOrderSubsidyDetailDTO> ownerOrderSubsidyDetailDTOS) {
		this.ownerOrderSubsidyDetailDTOS = ownerOrderSubsidyDetailDTOS;
	}
	public OwnerOrderCostDTO getOwnerOrderCostDTO() {
		return ownerOrderCostDTO;
	}
	public void setOwnerOrderCostDTO(OwnerOrderCostDTO ownerOrderCostDTO) {
		this.ownerOrderCostDTO = ownerOrderCostDTO;
	}
	public List<AccountOwnerIncomeExamineDTO> getAccountOwnerIncomeExamineDTOS() {
		return accountOwnerIncomeExamineDTOS;
	}
	public void setAccountOwnerIncomeExamineDTOS(List<AccountOwnerIncomeExamineDTO> accountOwnerIncomeExamineDTOS) {
		this.accountOwnerIncomeExamineDTOS = accountOwnerIncomeExamineDTOS;
	}
	public List<RenterOrderChangeApplyDTO> getRenterOrderChangeApplyDTOS() {
		return renterOrderChangeApplyDTOS;
	}
	public void setRenterOrderChangeApplyDTOS(List<RenterOrderChangeApplyDTO> renterOrderChangeApplyDTOS) {
		this.renterOrderChangeApplyDTOS = renterOrderChangeApplyDTOS;
	}
	public AccountRenterDepositDTO getAccountRenterDepositDTO() {
		return accountRenterDepositDTO;
	}
	public void setAccountRenterDepositDTO(AccountRenterDepositDTO accountRenterDepositDTO) {
		this.accountRenterDepositDTO = accountRenterDepositDTO;
	}
	public AccountRenterWzDepositDTO getAccountRenterWzDepositDTO() {
		return accountRenterWzDepositDTO;
	}
	public void setAccountRenterWzDepositDTO(AccountRenterWzDepositDTO accountRenterWzDepositDTO) {
		this.accountRenterWzDepositDTO = accountRenterWzDepositDTO;
	}
	public List<AccountRenterCostDetailDTO> getAccountRenterCostDetailDTO() {
		return accountRenterCostDetailDTO;
	}
	public void setAccountRenterCostDetailDTO(List<AccountRenterCostDetailDTO> accountRenterCostDetailDTO) {
		this.accountRenterCostDetailDTO = accountRenterCostDetailDTO;
	}
	public OrderRefundRecordDTO getOrderRefundRecordDTO() {
		return orderRefundRecordDTO;
	}
	public void setOrderRefundRecordDTO(OrderRefundRecordDTO orderRefundRecordDTO) {
		this.orderRefundRecordDTO = orderRefundRecordDTO;
	}
	public RenterDeliveryAddrDTO getRenterDeliveryAddrDTO() {
		return renterDeliveryAddrDTO;
	}
	public void setRenterDeliveryAddrDTO(RenterDeliveryAddrDTO renterDeliveryAddrDTO) {
		this.renterDeliveryAddrDTO = renterDeliveryAddrDTO;
	}
	public String getChangeApplyRenterOrderNo() {
		return changeApplyRenterOrderNo;
	}
	public void setChangeApplyRenterOrderNo(String changeApplyRenterOrderNo) {
		this.changeApplyRenterOrderNo = changeApplyRenterOrderNo;
	}
	public boolean isChangeApply() {
		return isChangeApply;
	}
	public void setChangeApply(boolean isChangeApply) {
		this.isChangeApply = isChangeApply;
	}
	public boolean isAutoRefuse() {
		return isAutoRefuse;
	}
	public void setAutoRefuse(boolean isAutoRefuse) {
		this.isAutoRefuse = isAutoRefuse;
	}
	public RenterOrderChangeApplyDTO getRenterOrderChangeApplyDTO() {
		return renterOrderChangeApplyDTO;
	}
	public void setRenterOrderChangeApplyDTO(RenterOrderChangeApplyDTO renterOrderChangeApplyDTO) {
		this.renterOrderChangeApplyDTO = renterOrderChangeApplyDTO;
	}
	public Integer getChangeApplyPreIncomAmt() {
		return changeApplyPreIncomAmt;
	}
	public void setChangeApplyPreIncomAmt(Integer changeApplyPreIncomAmt) {
		this.changeApplyPreIncomAmt = changeApplyPreIncomAmt;
	}
	public Integer getOwnerPreIncom() {
		return ownerPreIncom;
	}
	public void setOwnerPreIncom(Integer ownerPreIncom) {
		this.ownerPreIncom = ownerPreIncom;
	}
	public OrderStopFreightDTO getOrderStopFreightDTO() {
		return orderStopFreightDTO;
	}
	public void setOrderStopFreightDTO(OrderStopFreightDTO orderStopFreightDTO) {
		this.orderStopFreightDTO = orderStopFreightDTO;
	}
	public SectionDeliveryVO getSectionDelivery() {
		return sectionDelivery;
	}
	public void setSectionDelivery(SectionDeliveryVO sectionDelivery) {
		this.sectionDelivery = sectionDelivery;
	}
	public WzQueryDayConfDTO getWzQueryDayConfDTO() {
		return wzQueryDayConfDTO;
	}
	public void setWzQueryDayConfDTO(WzQueryDayConfDTO wzQueryDayConfDTO) {
		this.wzQueryDayConfDTO = wzQueryDayConfDTO;
	}

}
