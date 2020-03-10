package com.atzuche.order.commons.entity.orderDetailDto;
import com.atzuche.order.commons.entity.dto.RenterOrderSubsidyDetailDTO;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
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
    public OrderCancelReasonDTO orderCancelReason;
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




}
