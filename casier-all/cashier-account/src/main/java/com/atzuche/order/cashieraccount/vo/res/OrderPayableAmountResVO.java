package com.atzuche.order.cashieraccount.vo.res;

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
    private Integer amt;
    /**
     * 代付费用明细
     */
    private List<AccountPayAbleResVO> accountPayAbles;




}
