package com.atzuche.order.settle.vo.req;

import lombok.Data;


/**
 * 取消订单 计算费用统计
 */
@Data
public class SettleCancelOrdersAccount {


    /** ***********************************************************************************车主端统计费用 统计费用 */

    /**
     * 车主罚金金额
     */
    private int ownerFineAmt;

    /**************************************************************************************** 租客端统计费用 */
    /**
     * 租客罚金金额
     */
    private int rentFineAmt;
    /**
     * 租客实付租车费用
     */
    private int rentCostAmt;

    /**
     * 租客实付租车押金
     */
    private int rentDepositAmt;

    /**
     * 租客实付违章押金
     */
    private int rentWzDepositAmt;

    /**
     * 租客实付钱包金额
     */
    private int renWalletAmt;

    /**
     * 租客实付凹凸币金额
     */
    private int renCoinAmt;
}
