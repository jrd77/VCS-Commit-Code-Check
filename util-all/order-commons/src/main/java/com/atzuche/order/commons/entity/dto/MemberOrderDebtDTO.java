package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.io.Serializable;
@Data
public class MemberOrderDebtDTO implements Serializable {
    @AutoDocProperty("订单号")
    private String orderNo;
    @AutoDocProperty("下单时间")
    private String createOrderTime;
    @AutoDocProperty("订单开始时间")
    private String orderStartTime;
    @AutoDocProperty("订单结束时间")
    private String orderEndTime;
    @AutoDocProperty("订单欠款金额")
    private Integer debtAmt;
}
