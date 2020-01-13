package com.atzuche.order.admin.vo.req.payment;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by qincai.lin on 2019/12/30.
 */
@Data
@ToString
public class PaymentRequestVO implements Serializable {
    @AutoDocProperty(value = "订单号")
    @NotBlank(message="订单号不能为空")
    private String orderNo;
    
}
