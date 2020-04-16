package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

@Data
public class OwnerOrderIncrementDetailDTO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 子订单号
     */
    private String ownerOrderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 费用编码
     */
    private String costCode;
    /**
     * 费用描述
     */
    private String costDesc;
    /**
     * 单价
     */
    private Integer unitPrice;
    /**
     * 份数
     */
    private Double count;
    /**
     * 总价
     */
    private Integer totalAmount;

}