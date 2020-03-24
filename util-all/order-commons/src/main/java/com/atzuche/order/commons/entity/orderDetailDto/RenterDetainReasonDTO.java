package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

/**
 * 租车押金暂扣原因
 *
 * @author pengcheng.fu
 * @date 2020/3/24 14:40
 */
@Data
public class RenterDetainReasonDTO {

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 暂扣类型编码
     */
    private String detainTypeCode;
    /**
     * 暂扣类型名称
     */
    private String detainTypeName;
    /**
     * 暂扣原因编码
     */
    private String detainReasonCode;
    /**
     * 暂扣原因描述
     */
    private String detainReasonName;
    /**
     * 是否暂扣:0,否 1,是
     */
    private Integer detainStatus;
}
