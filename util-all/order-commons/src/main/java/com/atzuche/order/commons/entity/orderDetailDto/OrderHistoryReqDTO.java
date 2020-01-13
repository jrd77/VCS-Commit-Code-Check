package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class OrderHistoryReqDTO {
    /**
     * 主订单号
     */
    @NotBlank(message = "主订单号不能为空")
    @NotNull(message = "主订单号不能为空")
    private String orderNo;
    /**
     * 是否需要租客历史订单 true:需要  false：不需要
     */
    @NotBlank(message = "是否需要租客历史订单不能为空")
    @NotNull(message = "是否需要租客历史订单不能为空")
    private boolean isNeedRenterOrderHistory;
    /**
     * 是否需要车主历史订单 true：需要  false：不需要
     */
    @NotBlank(message = "是否需要车主历史订单不能为空")
    @NotNull(message = "是否需要车主历史订单不能为空")
    private boolean isNeedOwnerOrderHistory;

}
