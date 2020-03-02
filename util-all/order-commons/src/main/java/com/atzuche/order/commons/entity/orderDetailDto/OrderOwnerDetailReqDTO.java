package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class OrderOwnerDetailReqDTO {
    /**
     * 主订单号
     */
    @NotBlank(message = "主订单号不能为空")
    @NotNull(message = "主订单号不能为空")
    private String orderNo;
    /**
     * 车主子订单号
     */
    private String ownerOrderNo;
    /*
    * 车主会员号
    * */
    private String ownerMemNo;

}
