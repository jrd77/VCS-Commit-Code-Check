package com.atzuche.order.cashieraccount.vo.res.pay;

import lombok.Data;

/**
 * 支付系统回调 同步信息返回
 */
@Data
public class OrderPaySynResVO extends OrderPayBaseResVO {

    /**
     * refundId 退款Id
     */
    private int refundId;

    private int refundAmt;

    private int payAmt;

    private String errorCode;

    private String errorMessage;

    private String tn;

    private String appId;

    private String nonceStr;

    private String timeStamp;

    private String Package;

    private String signType;

    private String paySign;


}
