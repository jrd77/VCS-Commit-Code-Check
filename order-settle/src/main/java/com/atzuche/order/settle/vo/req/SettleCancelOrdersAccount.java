package com.atzuche.order.settle.vo.req;

import lombok.Data;


/**
 * 取消订单 计算费用统计
 */
@Data
public class SettleCancelOrdersAccount {


    /** ***********************************************************************************平台费用统计 统计费用 */

    /**
     * 平台收入罚金
     */
    private int platformFineImconeAmt;
    /** ***********************************************************************************车主端统计费用 统计费用 */

    /**
     * 车主罚金金额
     */
    private int ownerFineAmt;

    /**
     * 车主收入罚金金额 （来自租客或者平台的罚金补贴）
     */
    private int ownerFineIncomeAmt;

    /**************************************************************************************** 租客端统计费用 */
    /**
     * 租客罚金金额
     */
    private int rentFineAmt;
    /**
     * 租客收入罚金金额（来自车主或者平台的罚金补贴）
     */
    private int rentFineIncomeAmt;

    /**
     * 租客实付租车费用
     */
    private int rentCostAmt;

    /**
     * 租客实付剩余租车费用
     */
    private int rentSurplusCostAmt;

    /**
     * 租客实付租车押金
     */
    private int rentDepositAmt;

    /**
     * 租客实付剩余租车押金
     */
    private int rentSurplusDepositAmt;

    /**
     * 租客实付违章押金
     */
    private int rentWzDepositAmt;

    /**
     * 租客实付剩余违章押金
     */
    private int rentSurplusWzDepositAmt;

    /**
     * 租客实付钱包金额
     */
    private int renWalletAmt;

    /**
     * 租客实付剩余钱包金额
     */
    private int rentSurplusWalletAmt;

    /**
     * 租客实付凹凸币金额
     */
    private int renCoinAmt;
}
