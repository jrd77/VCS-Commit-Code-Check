package com.atzuche.order.commons.entity.ownerOrderDetail;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class RenterOwnerPriceDTO {
    @AutoDocProperty("租客对车主的调价（明细）")
    private Integer renterToOwnerPrice;
    @AutoDocProperty("车主对租客的调价（明细）")
    private Integer ownerToRenterPrice;

}
