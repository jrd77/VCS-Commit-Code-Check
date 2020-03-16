package com.atzuche.order.open.vo;

import com.autoyol.doc.annotation.AutoDocProperty;
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
    @AutoDocProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    @AutoDocProperty(value = "虚拟支付账号，即成本来源")
    @NotBlank(message = "虚拟支付账号不能为空")
    private String accountNo;
    @NotNull(message = "支付金额不能为空")
    @AutoDocProperty(value = "支付金额")
    private Integer payAmt;
    @AutoDocProperty(value = "费用类型,11:租车费用,01:车辆押金,02:违章押金")
    @NotBlank(message = "费用类型不能为空")
    private String  cashType;
}
