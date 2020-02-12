package com.atzuche.order.settle.vo.req;


import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 租客费用信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundApplyVO {
    private SettleOrders settleOrders;
    private int refundAmt;
    private RenterCashCodeEnum renterCashCodeEnum;
    private String remarke;
}
