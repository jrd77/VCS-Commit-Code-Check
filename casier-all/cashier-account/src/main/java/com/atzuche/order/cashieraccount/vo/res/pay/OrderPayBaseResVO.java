package com.atzuche.order.cashieraccount.vo.res.pay;

import lombok.Data;

/**
 * 支付系统请求签名参数
 */
@Data
public class OrderPayBaseResVO {
    /**
     * atappId 凹凸APPID
     */
    private String atappId;

    /**
     *  支付ID
     */
    private int payId;

    /**
     *  支付项目
     */
    private String payKind;
    /**
     *  支付类型
     */
    private String payType         ;
    /**
     *  支付来源
     */
    private String paySource;

    /**
     * payEnv 支付环境
     */
    private String payEnv;

    /**
     * OS
     */
    private String OS;

    /**
     * 请求ip
     */
    private String reqIp;


    /**
     * internaNo
     */
    private String internaNo;

    /**
     * 会员号
     */
    private String menNo;
    /**
     * orderNo
     */
    private String orderNo;

    /**
     * 补付第几笔
     */
    private String paySn;
    /**
     * 签名
     */
    private String atpaySign;

    /**
     * 补付第几笔
     */
    private String payMd5;


    private String respMsg;

    private String respCode;

    /**
     * 支付流水号
     */
    private String qn;

    /**
     * 状态
     */
    private String transStatus;


}
