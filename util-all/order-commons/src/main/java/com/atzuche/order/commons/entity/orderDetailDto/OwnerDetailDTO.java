package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class OwnerDetailDTO {
    @AutoDocProperty("车主订单")
    private OwnerOrderDTO ownerOrderDTO;
    @AutoDocProperty("车主会员详情")
    private OwnerMemberDTO ownerMemberDTO;
    @AutoDocProperty("车主商品详情")
    private OwnerGoodsDTO ownerGoodsDTO;
}
