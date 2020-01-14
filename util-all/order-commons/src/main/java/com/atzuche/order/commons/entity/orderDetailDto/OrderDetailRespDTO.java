package com.atzuche.order.commons.entity.orderDetailDto;
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

}
