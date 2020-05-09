package com.atzuche.order.settle.vo.res;

import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import lombok.Data;

/**
 * 订单结算抵扣欠款返回信息
 *
 * @author pengcheng.fu
 * @date 2020/4/17 17:36
 */
@Data
public class OrderSettleResVO {

    /**
     * 订单结算状态
     */
    private SettleStatusEnum settleStatus;

    /**
     * 新系统抵扣金额
     */
    private Integer newTotalRealDebtAmt;

    /**
     * 老系统抵扣金额
     */
    private Integer oldTotalRealDebtAmt;

    public OrderSettleResVO() {
    }

    public OrderSettleResVO(SettleStatusEnum settleStatus, Integer newTotalRealDebtAmt, Integer oldTotalRealDebtAmt) {
        this.settleStatus = settleStatus;
        this.newTotalRealDebtAmt = newTotalRealDebtAmt;
        this.oldTotalRealDebtAmt = oldTotalRealDebtAmt;
    }
}
