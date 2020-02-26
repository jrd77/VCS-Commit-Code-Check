package com.atzuche.order.commons.entity.orderDetailDto;

import com.atzuche.order.commons.entity.dto.RenterOrderSubsidyDetailDTO;
import lombok.Data;

import java.util.List;

@Data
public class RenterOrderDetailRespDTO {
    public OrderDTO order;
    public RenterOrderDTO renterOrder;
    public RenterGoodsDTO renterGoods;
    public RenterOrderDeliveryDTO renterOrderDeliveryGet;
    public RenterOrderDeliveryDTO renterOrderDeliveryReturn;
    public RenterMemberDTO renterMember;
    public RenterOrderCostDTO renterOrderCost;
    public RenterHandoverCarInfoDTO renterHandoverCarInfo;
    public List<RenterMemberRightDTO> renterMemberRightList;
    public List<RenterOrderCostDetailDTO> renterOrderCostDetailList;
    public List<RenterAdditionalDriverDTO> renterAdditionalDriverList;
    public List<RenterOrderFineDeatailDTO> renterOrderFineDeatailList;
    public List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOS;
}
