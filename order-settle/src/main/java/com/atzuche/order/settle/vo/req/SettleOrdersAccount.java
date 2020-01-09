package com.atzuche.order.settle.vo.req;

import lombok.Data;


/**
 * 计算费用统计
 */
@Data
public class SettleOrdersAccount {



    /** ***********************************************************************************车主端统计费用 统计费用 */

    /**
     * 车主收益金额
     */
    private int ownerCostAmtFinal;
    /**
     * 车主最终收益金额
     */
    private int ownerCostSurplusAmt;

    /**
     * 租客应付费用
     */
    private int rentCostAmtFinal;
    /**
     * 租客实付付费用 （在实付小于应付情况下 包含真实消费金额 + 押金抵扣租车费用部分 + 转移历史欠款冲正，用于计算部落库）
     * （实付大于应付时候 ，为真实消费金额）
     */
    private int rentCostPayAmt;
    /**
     * 当 租车费用 实付大于应付时候 有效
     * 剩余租车费用 即应退
     */
    private int rentCostSurplusAmt;

    /**
     * 租客实付 车辆押金
     */
    private int depositAmt;

    /**
     * 剩余 车辆押金
     */
    private int depositSurplusAmt;

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 租客订单号
     */
    private String renterOrderNo;
    /**
     * 车主订单号
     */
    private String ownerOrderNo;

    /**
     * 租客会员号
     */
    private String renterMemNo;

    /**
     * 车主会员号
     */
    private String ownerMemNo;

}
