package com.atzuche.order.open.vo;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/7 2:25 下午
 **/
@ToString
@Data
public  class RenterCostShortDetail {
    @AutoDocProperty(value = "订单号")
    private String orderNo;
    @AutoDocProperty(value = "租车总费用，不包括罚金")
    private int totalRentCostAmt;
    @AutoDocProperty(value = "罚金总额")
    private int totalFineAmt;
    @AutoDocProperty(value = "待支付押金总额")
    private int toPayDeposit;
    @AutoDocProperty(value = "待支付违章押金总额")
    private int toPayWzDeposit;
    @AutoDocProperty(value = "押金预计退还")
    private int expReturnDeposit;
    @AutoDocProperty(value = "违章押金预计退还")
    private int expReturnWzDeposit;
}
