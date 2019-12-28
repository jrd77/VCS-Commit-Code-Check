package com.atzuche.delivery.vo.delivery;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 胡春林
 * 取还车信息
 */
@Data
@ToString
public class OrderDeliveryVO implements Serializable {

    private OrderDeliveryDTO orderDeliveryDTO;
    private RenYunFlowOrderDTO renYunFlowOrderDTO;
}
