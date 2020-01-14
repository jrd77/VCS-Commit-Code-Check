package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

@Data
public class RenterDepositDetailDTO {

    private String orderNo;
    /**
     * X系数（押金计算使用）
     */
    private Integer suggestTotal;
    /**
     * 新车押金系数（押金计算使用）
     */
    private Double newCarCoefficient;
    /**
     * 品牌车押金系数（押金计算使用）
     */
    private Double carSpecialCoefficient;
    /**
     * 原始租车押金
     */
    private Integer originalDepositAmt;
    /**
     * 减免比例
     */
    private Double reductionRate;
    /**
     * 减免押金金额
     */
    private Integer reductionDepositAmt;

}
