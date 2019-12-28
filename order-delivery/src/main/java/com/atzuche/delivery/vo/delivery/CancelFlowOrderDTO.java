package com.atzuche.delivery.vo.delivery;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 胡春林
 */
@Data
@ToString
public class CancelFlowOrderDTO implements Serializable {

    /**
     * 订单编号
     **/
    private String ordernumber;
    /**
     * 服务类型（take:取车服务 back:还车服务）
     **/
    private String servicetype;
    /**
     * 签名
     **/
    private String sign;
}
