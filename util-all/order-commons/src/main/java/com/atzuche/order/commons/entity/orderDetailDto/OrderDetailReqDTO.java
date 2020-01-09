package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class OrderDetailReqDTO {
    /**
     * 主订单号
     */
    @NotBlank(message = "主订单号不能为空")
    @NotNull(message = "主订单号不能为空")
    private String orderNo;
    /**
     * 租客子订单号
     */
    private String renterOrderNo;
    /**
     * 车主子订单号
     */
    private String ownerOrderNo;

}
