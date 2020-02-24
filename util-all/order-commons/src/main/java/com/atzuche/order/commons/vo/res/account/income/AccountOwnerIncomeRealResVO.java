package com.atzuche.order.commons.vo.res.account.income;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

/**
 * 订单车主 真实收益
 * @author haibao.yan
 */
@Data
public class AccountOwnerIncomeRealResVO {

    /**
     * 订单号
     */
    @AutoDocProperty("订单号")
    private String orderNo;

    /**
     * 车主订单真实收益总额
     */
    @AutoDocProperty("车主订单真实收益总额")
    private int incomeAmt;





    /**
     * 车主订单完结待审核收益总额
     */
    @AutoDocProperty("车主订单完结待审核收益总额")
    private int incomeExamineAmt;


    /**
     * 油费
     */
    @AutoDocProperty("油费")
    private int oilCost;

    /**
     * 租客给车主调价
     */
    @AutoDocProperty("租客给车主调价")
    private int renterAdjustPrice;

    /**
     * 车主给租客调价
     */
    @AutoDocProperty("租客给车主调价")
    private int ownerAdjustPrice;

    /**
     * 取还车服务违约金
     */
    @AutoDocProperty("取还车服务违约金")
    private int ownerModifySrvAddrCost;

    /**
     * 提前还车违约金
     */
    @AutoDocProperty("提前还车违约金")
    private int fineAmt;

    /**
     * 违约金收益
     */
    @AutoDocProperty("违约金收益")
    private int fineYield;

    /**
     * 停运费
     */
    @AutoDocProperty("停运费")
    private int offStreamCost;

    /**
     * 车主支付给平台的费用
     */
    @AutoDocProperty("车主支付给平台的费用")
    private int ownerPayPlatform;

    /**
     * 历史欠款
     */
    @AutoDocProperty("历史欠款")
    private int ownerDebt;

    /**
     * 取消订单违约金
     */
    @AutoDocProperty("取消订单违约金")
    private int cancelOrderPenalty;

    /**
     * 平台加油服务费
     */
    @AutoDocProperty("平台加油服务费")
    private int platformRefuelServiceCharge;
}
