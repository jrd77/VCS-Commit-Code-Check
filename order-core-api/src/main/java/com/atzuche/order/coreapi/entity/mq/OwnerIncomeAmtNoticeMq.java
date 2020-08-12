package com.atzuche.order.coreapi.entity.mq;

import lombok.Data;

/**
 * @author pengcheng.fu
 * @date 2020/7/7 10:48
 */

@Data
public class OwnerIncomeAmtNoticeMq {

    /**
     * 会员号
     */
    private String memNo;

    /**
     * 上账金额(可提现金额)
     */
    private String receiveAmt;
}
