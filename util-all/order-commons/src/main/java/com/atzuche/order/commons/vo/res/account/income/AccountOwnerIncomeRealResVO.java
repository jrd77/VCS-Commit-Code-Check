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
     * 取消订单违约金
     */
    @AutoDocProperty("取消订单违约金")
    private int cancelFineAmt;

    /**
     * 取换车服务违约金
     */
    @AutoDocProperty("取换车服务违约金")
    private int getCarAndReturnCarFineAmt;

    /**
     * 车主订单完结待审核收益总额
     */
    @AutoDocProperty("车主订单完结待审核收益总额")
    private int incomeExamineAmt;

//    /**
//     * 车主收益明细
//     */
//    @AutoDocProperty("车主收益明细")
//    List<AccountOwnerIncomeDetailEntity> accountOwnerIncomeDetails;
//    /**
//     * 车主待审核收益明细
//     */
//    @AutoDocProperty("车主待审核收益明细")
//    List<AccountOwnerIncomeExamineEntity> accountOwnerIncomeExamines;




}
