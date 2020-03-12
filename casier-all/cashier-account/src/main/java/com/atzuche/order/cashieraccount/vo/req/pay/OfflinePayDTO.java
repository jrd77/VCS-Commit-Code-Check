package com.atzuche.order.cashieraccount.vo.req.pay;

import com.atzuche.order.cashieraccount.common.PayCashTypeEnum;
import com.atzuche.order.commons.enums.cashier.PaySourceEnum;
import com.atzuche.order.commons.enums.cashier.PayTypeEnum;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/11 5:57 下午
 **/
@Data
@ToString
public class OfflinePayDTO implements Serializable {
    @AutoDocProperty(value = "订单号")
    private String orderNo;

    @AutoDocProperty(value = "支付金额")
    private Integer payAmt;

    private PayCashTypeEnum cashType;
    @AutoDocProperty(value = "支付渠道")
    private String payChannel;
    @AutoDocProperty(value = "支付流水号")
    private String qn;
    @AutoDocProperty(value = "支付Id")
    private String internalNo;

    @AutoDocProperty(value = "支付类型:01消费，02预授权")
    private PayTypeEnum payType;

    private String memNo;

    private String renterNo;

    private PaySourceEnum paySource;
}
