package com.atzuche.order.delivery.vo.delivery;

import com.atzuche.order.delivery.vo.delivery.rep.OwnerGetAndReturnCarDTO;
import com.atzuche.order.delivery.vo.delivery.rep.RenterGetAndReturnCarDTO;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.*;

/**
 * @author 胡春林
 * 车主 租客相关费用信息
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryOilCostVO {

    @AutoDocProperty("车主取还车服务")
    OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO;
    @AutoDocProperty("租客取还车服务")
    RenterGetAndReturnCarDTO renterGetAndReturnCarDTO;
}
