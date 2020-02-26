package com.atzuche.order.commons.entity.orderDetailDto;

import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.autoyol.doc.annotation.AutoDocProperty;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import lombok.Data;

@Data
public class RenterDetailDTO {

    @AutoDocProperty("租客订单")
    private RenterOrderDTO renterOrderDTO;
    @AutoDocProperty("租客会员详情")
    private RenterMemberDTO renterMemberDTO;
    @AutoDocProperty("租客会员详情")
    private RenterGoodsDetailDTO renterGoodsDetail;
}
