package com.atzuche.order.coreapi.entity.mq;

import lombok.Data;

/**
 * @author pengcheng.fu
 * @date 2020/7/315:04
 */

@Data
public class WithdrawalsNoticeMq {

    /**
     * 凹凸提现流水号
     */
    private String serialNumber;

    /**
     * 会员号
     */
    private String memNo;

    /**
     * 提现金额
     */
    private String amt;

    /**
     * 交易状态:1-成功 2-失败 3-未明
     */
    private String tranStatus;

    /**
     * 提现金额返还标识(提现失败返还之前冻结的金额):0-否 1-是
     */
    private String refundFlag;

    /**
     * 交易描述:失败原因
     */
    private String tranMeg;


}
