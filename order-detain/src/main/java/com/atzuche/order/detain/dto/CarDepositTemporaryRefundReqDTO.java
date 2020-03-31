package com.atzuche.order.detain.dto;


import lombok.Data;
import lombok.ToString;

/**
 * @author pengcheng.fu
 * @date 2020/3/24 11:11
 */

@Data
@ToString
public class CarDepositTemporaryRefundReqDTO {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 风控-暂扣车辆押金 0-否 1-是
     */
    private String fkDetainFlag;

    /**
     * 风控-暂扣原因
     */
    private String fkDetainReason;

    /**
     * 交易-暂扣车辆押金 0-否 1-是
     */
    private String jyDetainFlag;

    /**
     * 交易-暂扣车辆押金 0-否 1-是
     */
    private String jyDetainReason;

    /**
     * 理赔-暂扣车辆押金 0-否 1-是
     */
    private String lpDetainFlag;

    /**
     * 理赔-暂扣原因
     */
    private String lpDetainReason;


}
