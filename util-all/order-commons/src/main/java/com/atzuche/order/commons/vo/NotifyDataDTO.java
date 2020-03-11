package com.atzuche.order.commons.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/11 2:55 下午
 **/
@Data
@ToString
public class NotifyDataDTO implements Serializable {
    /**
     * appid
     */
    String atappId;
    /**
     * 支付ID
     */
    String payId;
    /**
     * 支付项
     */
    String payKind;
    /**
     * 支付类型
     */
    String payType;
    /**
     * 支付来源
     */
    String paySource;
    /**
     * 支付环境
     */
    String payEnv;
    /**
     * 来源系统
     */
    String reqOs;
    /**
     * 请求IP地址
     */
    String reqIp;
    /**
     * 版本号
     */
    String internalNo;  //版本号
    /**
     * 会员号
     */
    String memNo;
    /**
     * 订单号
     */
    String orderNo;
    /**
     * 签名串
     */
    String atpaySign;
    /**
     * 补付的记录号，第几笔补付记录
     */
    String paySn;
    /**
     * md5字符串，防重复,支付请求的时候传递过来的，原样返回。
     */
    String payMd5;
    /**
     * 透传参数
     */
    String extendParams;
    // ------------------------------------------------------
    /**
     * 支付或退款结算金额
     */
    private String settleAmount;
    /**
     * 支付或退款时间
     */
    private String orderTime;
    /**
     * 状态:00成功，01进行中，03失败
     */
    private String transStatus;
    /**
     * 流水号
     */
    private String qn;
    /**
     * 响应码
     */
    private String respCode;
    private String respMsg;
    /**
     * 累计冻结信用金额，单位为：元（人民币），精确到小数点后两位（信用授权场景返回）
     */
    private String totalFreezeCreditAmount;
    /**
     * 累计冻结自有资金金额，单位为：元（人民币），精确到小数点后两位（信用授权场景返回）
     */
    private String totalFreezeFundAmount;

    private String atpayNewTransId;

    /**
     * 线上支付或者线下、虚拟支付（0-线上支付，1-线下支付，2-虚拟支付）
     */
    private int payLine;
    /**
     * 虚拟支付对应的成本账号
     */
    private String virtualAccountNo;
    /**
     * 线下支付的渠道名称
     */
    private String payChannel;
}
