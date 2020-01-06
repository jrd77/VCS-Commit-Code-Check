package com.atzuche.order.admin.vo.req.delivery;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 前端配送地址相关修改
 */
@Data
@ToString
public class DeliveryReqVO {
    @AutoDocProperty("是否使用取车服务")
    private DeliveryReqDTO getDeliveryReqDTO;
    @AutoDocProperty("是否使用还车服务")
    private DeliveryReqDTO renterDeliveryReqDTO;
}
