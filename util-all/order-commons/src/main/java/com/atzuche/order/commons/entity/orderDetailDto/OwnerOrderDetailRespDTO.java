package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.util.List;

import com.atzuche.order.commons.vo.res.SectionDeliveryVO;

@Data
public class OwnerOrderDetailRespDTO {
    public OrderDTO order;
    public OrderStatusDTO orderStatus;
    public OrderSourceStatDTO orderSourceStat;
    public RenterMemberDTO renterMemberDTO;
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
    public List<OwnerOrderIncrementDetailDTO> ownerOrderIncrementDetailDTOS;
    // 区间配送信息
    public SectionDeliveryVO sectionDelivery;
}
