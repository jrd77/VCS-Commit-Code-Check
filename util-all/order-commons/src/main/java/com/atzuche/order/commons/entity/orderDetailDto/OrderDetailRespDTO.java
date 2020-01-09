package com.atzuche.order.commons.entity.orderDetailDto;
import lombok.Data;

import java.util.List;

@Data
public class OrderDetailRespDTO {
    private OrderDTO order;
    private RenterOrderDTO renterOrder;
    private OwnerOrderDTO ownerOrder;
    private OrderStatusDTO orderStatus;
    private OrderSourceStatDTO orderSourceStat;
    private RenterGoodsDTO renterGoods;
    private OwnerGoodsDTO ownerGoods;
    private RenterOrderDeliveryDTO renterOrderDeliveryGet;
    private RenterOrderDeliveryDTO renterOrderDeliveryReturn;
    private RenterMemberDTO renterMember;
    private OwnerMemberDTO ownerMember;
    private RenterOrderCostDTO renterOrderCost;
    private RenterHandoverCarInfoDTO renterHandoverCarInfo;
    private OrderCancelReasonDTO orderCancelReason;
    private List<OwnerMemberRightDTO> ownerMemberRightList;
    private List<RenterMemberRightDTO> renterMemberRightList;
    private List<RenterOrderCostDetailDTO> renterOrderCostDetailList;
    private List<AccountOwnerIncomeDetailDTO> accountOwnerIncomeDetailList;
    private List<OwnerOrderPurchaseDetailDTO> ownerOrderPurchaseDetailList;
    private List<ConsoleOwnerOrderFineDeatailDTO> consoleOwnerOrderFineDetailList;
}
