package com.atzuche.order.cashieraccount.vo.req.pay;

import com.atzuche.order.commons.enums.PayCashTypeEnum;
import com.atzuche.order.cashieraccount.common.VirtualAccountEnum;
import com.atzuche.order.cashieraccount.common.VirtualPayTypeEnum;
import lombok.Data;
import lombok.ToString;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/11 3:38 下午
 **/
@Data
@ToString
public class VirtualPayDTO {

    private String orderNo;
    private VirtualAccountEnum accountEnum;
    private String memNo;

    private String renterNo;

    private Integer payAmt;
    private PayCashTypeEnum cashType;
    private VirtualPayTypeEnum payType;

}
