package com.atzuche.order.rentermem.entity.dto;

import lombok.Data;

@Data
public class MemRightCarDepositAmtRespDTO {
    /**
     * 车辆押金减免比例
     */
    private Double reductionRate;
    /**
     * 减免的押金 reductionRate*originalDepositAmt
     */
    private Integer reductionDepositAmt;
    /**
     * 原始车辆押金
     */
    private Integer originalDepositAmt;
}
