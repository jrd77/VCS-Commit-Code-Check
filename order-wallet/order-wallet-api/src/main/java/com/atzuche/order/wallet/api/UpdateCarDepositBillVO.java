package com.atzuche.order.wallet.api;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/9 2:22 下午
 **/
@Data
@ToString
public class UpdateCarDepositBillVO {
    @NotBlank(message = "订单号")
    private String orderNo;
    @NotBlank(message = "车辆号")
    private String carNo;
    @NotNull(message = "新增的押金")
    private Integer updateBill;
}
