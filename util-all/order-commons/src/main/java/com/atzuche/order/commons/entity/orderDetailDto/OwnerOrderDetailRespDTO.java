package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.util.List;

@Data
public class OwnerOrderDetailRespDTO {
    public OrderDTO order;
    public OwnerOrderDTO ownerOrder;
    public OwnerGoodsDTO ownerGoods;
    public OwnerMemberDTO ownerMember;
    public OwnerHandoverCarInfoDTO ownerHandoverCarInfo;
    public List<OwnerMemberRightDTO> ownerMemberRightList;
    public List<AccountOwnerIncomeDetailDTO> accountOwnerIncomeDetailList;
    public List<OwnerOrderPurchaseDetailDTO> ownerOrderPurchaseDetailList;
    public List<ConsoleOwnerOrderFineDeatailDTO> consoleOwnerOrderFineDetailList;
    public List<OwnerOrderFineDeatailDTO> ownerOrderFineDeatailList;
    public List<OwnerOrderSubsidyDetailDTO> ownerOrderSubsidyDetailDTOS;
    public OwnerOrderCostDTO ownerOrderCostDTO;
    public List<AccountOwnerIncomeExamineDTO> accountOwnerIncomeExamineDTOS;
}
