package com.atzuche.order.commons.entity.trusteeship;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author 胡春林
 * 托管车参数
 */
@Data
@Slf4j
public class OrderCarTrusteeshipReqVO {

    @ApiModelProperty(value = "订单号", required = true)
    @NotBlank(message = "orderNo不能为空")
    private String orderNo;
    @ApiModelProperty(value = "车牌号", required = true)
    @NotBlank(message = "车牌号不能为空")
    private String carNo;
}
