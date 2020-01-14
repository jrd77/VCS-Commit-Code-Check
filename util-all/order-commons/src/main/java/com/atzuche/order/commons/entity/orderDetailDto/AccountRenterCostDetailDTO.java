package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountRenterCostDetailDTO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 支付来源code
     */
    private String paySourceCode;
    /**
     * 支付来源
     */
    private String paySource;
    /**
     * 支付渠道code
     */
    private String payTypeCode;
    /**
     * 支付渠道
     */
    private String payType;
    /**
     * 入账金额
     */
    private Integer amt;
    /**
     * 入账来源编码
     */
    private String sourceCode;
    /**
     * 入账来源编码描述
     */
    private String sourceDetail;
    /**
     * 交易时间
     */
    private LocalDateTime transTime;
    /**
     * 唯一标识
     */
    private String uniqueNo;
    /**
     * 入账时间
     */
    private LocalDateTime time;

}
