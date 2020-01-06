package com.atzuche.order.admin.vo.resp.delivery;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 取还车信息
 */
@Data
@ToString
public class DeliveryCarVO {

    @AutoDocProperty("取车服务信息")
    private GetHandoverCarDTO getHandoverCarDTO;
    @AutoDocProperty("还车服务信息")
    private ReturnHandoverCarDTO returnHandoverCarDTO;
    @AutoDocProperty("车主取还车服务")
    private OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO;
    @AutoDocProperty("租客取还车服务")
    private RenterGetAndReturnCarDTO renterGetAndReturnCarDTO;
}
