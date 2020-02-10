package com.atzuche.order.commons.entity.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class RentCityAndRiskAccidentReqDTO {
    /**
     * 主订单号
     */
    @NotNull(message = "订单号不能为空")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    /**
     * 租车城市（用车城市）
     */
    private String rentCity;
    /**
     * 是否有风控事故 0-否 1-是
     */
    private Integer isRiskAccident;
}
