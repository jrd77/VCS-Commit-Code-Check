package com.atzuche.order.cashieraccount.vo.res;

import com.atzuche.order.rentercost.entity.vo.PayableVO;
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
    private String memNo;
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 总待付款项
     */
    private Integer amtTotal;
    /**
     * 已付款项
     */
    private Integer amtPay;
    /**
     * 实际待款项
     */
    private Integer amt;
    /**
     * 名称
     */
    private String title;
    /**
     * 待付费用明细
     */
    private List<AccountPayAbleResVO> accountPayAbles;

    /**
     * 应付租车费用明细
     */
    List<PayableVO> payableVOs;

    /**
     * 应付租车费用
     */
    private Integer amtRent;
    /**
     * 应付租车车俩押金
     */
    private Integer amtDeposit;
    /**
     * 应付租车违章押金
     */
    private Integer amtWzDeposit;

    /**
     * 钱包抵扣金额
     */
    private int amtWallet;

    /**
     * 是否使用钱包 0-否，1-是
     */
    private Integer isUseWallet;
}
