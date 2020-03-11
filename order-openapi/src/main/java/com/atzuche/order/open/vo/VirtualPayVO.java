package com.atzuche.order.open.vo;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/10 2:48 下午
 **/
@Data
@ToString
public class VirtualPayVO {
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    @NotBlank(message = "虚拟支付账号")
    private String accountNo;
    @NotNull(message = "支付金额")
    private Integer payAmt;
    @NotBlank(message = "费用类型,11:租车费用,01:车辆押金,02:违章押金")
    private String  cashType;
}
