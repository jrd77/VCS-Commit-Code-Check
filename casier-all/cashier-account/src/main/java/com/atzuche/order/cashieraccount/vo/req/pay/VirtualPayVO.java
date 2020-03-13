package com.atzuche.order.cashieraccount.vo.req.pay;

import com.atzuche.order.cashieraccount.common.PayCashTypeEnum;
import lombok.Data;
import lombok.ToString;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/10 2:48 下午
 **/
@Data
@ToString
public class VirtualPayVO {
    private String orderNo;
    private String accountNo;
    private String accountName;
    private Integer payAmt;
    private PayCashTypeEnum cashType;
}
