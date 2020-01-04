package com.atzuche.order.settle.vo.req;

import lombok.Data;

/**
 * 计算费用统计
 */
@Data
public class SettleOrdersDefinition {

    /**
     * 结算 租客 、车主 费用明细
     */
    private SettleOrders settleOrders;

    /** 平台端 统计费用 */
    /**
     * 平台收益
     */
    private int platformProfitAmt;
    /**
     * 平台补贴
     */
    private int platformSubsidyAmt;

    /** 车主端统计费用 统计费用 */
    /**
     * 车主采购费
     */
    private int ownerCostAmt;
    /**
     * 车主补贴
     */
    private int ownerSubsidyAmt;

    /** 租客端统计费用 */
    /**
     * 租客费用
     */
    private int rentCostAmt;
    /**
     * 租客补贴
     */
    private int rentSubsidyAmt;
}
