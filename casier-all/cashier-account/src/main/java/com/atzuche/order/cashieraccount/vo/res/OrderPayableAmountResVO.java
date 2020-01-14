package com.atzuche.order.cashieraccount.vo.res;

import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

/**
 * 个人代付信息
 * @author haibao.yan
 */
@Data
public class OrderPayableAmountResVO {

    /**
     * 会员号
     */
    @AutoDocProperty("会员号")
    private String memNo;
    /**
     * 主订单号
     */
    @AutoDocProperty("主订单号")
    private String orderNo;
    /**
     * 总待付款项
     */
    @AutoDocProperty("总待付款项")
    private int amtTotal;
    /**
     * 已付款项
     */
    @AutoDocProperty("已付款项")
    private int amtPay;
    /**
     * 实际真实待款项
     */
    @AutoDocProperty("实际真实待款项")
    private int amt;
    /**
     * 支付标题
     */
    @AutoDocProperty("支付标题")
    private String title;
    /**
     * 待付费用明细（包含应付租车费用明细）
     */
    @AutoDocProperty("待付费用明细（包含应付租车费用明细）")
    private List<AccountPayAbleResVO> accountPayAbles;

    /**
     * 待付费用明细（包含应付租车费用明细）
     */
    @AutoDocProperty("是否补付 1 是 0 否")
    private Integer isPayAgain;

    /**
     * 应付租车费用明细
     */
    @AutoDocProperty("应付租车费用明细")
    List<PayableVO> payableVOs;

    /**
     * 应付租车费用
     */
    @AutoDocProperty("应付租车费用")
    private int amtRent;
    /**
     * 应付租车车俩押金
     */
    @AutoDocProperty("应付租车车俩押金")
    private int amtDeposit;
    /**
     * 应付租车违章押金
     */
    @AutoDocProperty("应付租车违章押金")
    private int amtWzDeposit;

    /**
     * 钱包抵扣金额
     */
    @AutoDocProperty("钱包抵扣金额")
    private int amtWallet;

    /**
     * 是否使用钱包 0-否，1-是
     */
    @AutoDocProperty("是否使用钱包 0-否，1-是")
    private int isUseWallet;
}
