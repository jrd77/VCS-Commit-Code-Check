package com.atzuche.order.open.vo;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/11 5:29 下午
 **/
@Data
@ToString
public class OfflinePayVO implements Serializable {
    @AutoDocProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    @NotNull(message = "支付金额不能为空")
    @AutoDocProperty(value = "支付金额")
    private Integer payAmt;
    @AutoDocProperty(value = "费用类型,11:租车费用,01:车辆押金,02:违章押金")
    @NotBlank(message = "费用类型不能为空")
    private String  cashType;
    @AutoDocProperty(value = "支付渠道")
    private String payChannel;
    @AutoDocProperty(value = "支付流水号")
    @NotBlank(message = "支付流水号不能为空")
    private String qn;
    @AutoDocProperty(value = "支付Id")
    @NotBlank(message = "支付ID不能为空")
    private String internalNo;

    @AutoDocProperty(value = "支付类型:01消费，02预授权")
    @NotBlank(message = "支付类型不能为空")
    private String payType;
    @AutoDocProperty(value = "支付来源:")
    @NotBlank(message = "支付来源不能为空")
    private String paySource;



}
