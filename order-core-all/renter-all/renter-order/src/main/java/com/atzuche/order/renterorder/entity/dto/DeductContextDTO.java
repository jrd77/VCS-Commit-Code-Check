package com.atzuche.order.renterorder.entity.dto;

import lombok.Data;

/**
 * 下单前费用计算抵扣信息公共参数抽取
 *
 * @author pengcheng.fu
 * @date 2020/1/14 10:43
 */
@Data
public class DeductContextDTO {

    /**
     * 原始租金
     */
    private Integer originalRentAmt;

    /**
     * 剩余租金
     */
    private Integer surplusRentAmt;

    /**
     * 取车服务费(取送服务券包含超运能溢价)
     */
    private Integer srvGetCost;

    /**
     * 还车服务费(取送服务券包含超运能溢价)
     */
    private Integer srvReturnCost;

    /**
     * OsTypeEnum.osVal
     */
    private String osVal;

}
