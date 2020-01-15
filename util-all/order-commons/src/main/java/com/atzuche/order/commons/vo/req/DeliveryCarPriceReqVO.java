package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class DeliveryCarPriceReqVO {

    @AutoDocProperty(value = "订单号", required = true)
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    @AutoDocProperty(value = "订单号", required = true)
    @NotNull(message = "燃油类型不能为空")
    private Integer carEngineType;
}
