package com.atzuche.order.wallet.server.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * withdrawal_log
 * @author 
 */
@Data
@ToString
public class WithdrawalLogEntity implements Serializable {
    private Integer id;

    /**
     * 提现金额
     */
    private Integer amt;

    /**
     * 1:中国银行,2:中国工商银行,3:中国农业银行,4:中国建设银行,5:中国邮政储蓄银行,6:交通银行,7:招商银行,8:浦发银行,9:兴业银行,10:华夏银行,11:广发银行,12:中信银行,13:中国光大银行,14:上海银行,15:上海农商银行,16:宁波银行,17:中国民生银行,18:杭州银行,19:南京银行
     */
    private Integer bankName;

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 流水号
     */
    private String serialNumber;

    /**
     * 用户注册号
     */
    private Integer memNo;

    /**
     * 用户手机号
     */
    private Long mobile;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 持卡人姓名
     */
    private String cardHolder;

    /**
     * 开户支行名称
     */
    private String branchBankName;

    /**
     * 开户省份
     */
    private String province;

    /**
     * 开户城市
     */
    private String city;

    /**
     * 0:未处理，1:已处理，2:暂停,11连接失败，12打款中，13打款成功，14打款失败，15人工处理。
     */
    private Integer status;

    /**
     * 提现时间
     */
    private Date createTime;

    /**
     * 处理时间
     */
    private Date updateTime;

    /**
     * 是否导入数据0否、1是
     */
    private Integer importFlag;

    /**
     * 请求返回码，默认值1, 0:成功，-1:提交主机失败,-2:执行失败,-3:数据格式错误,-4:尚未登录系统,-5:请求太频繁,-6:不是证书卡用户,-7:用户取消操作,-9:其它错误,-10:金额错误
     */
    private Integer resCode;

    /**
     * 请求返回码文本
     */
    private String resCodeTxt;

    /**
     * 请求返回状态
     */
    private String resMsg;

    /**
     * 错误码
     */
    private String errCode;

    /**
     * 错误文本
     */
    private String errTxt;

    /**
     * 查询返回码，默认值1, 0:成功，-1:提交主机失败,-2:执行失败,-3:数据格式错误,-4:尚未登录系统,-5:请求太频繁,-6:不是证书卡用户,-7:用户取消操作,-9:其它错误,-10:金额错误
     */
    private Integer queryCode;

    /**
     * 查询返回码文本
     */
    private String queryCodeTxt;

    /**
     * 流程提示
     */
    private String flowReqstsTxt;

    /**
     * 流程结果
     */
    private String flowRtnflgTxt;

    /**
     * 打款中状态查询权重，权重越大优先查询
     */
    private Integer weight;

    /**
     * 0无，1开始握手，2完成握手（推列表）
     */
    private Integer handshakePushFlag;

    /**
     * 0无，1开始握手，2完成握手（拉状态）
     */
    private Integer handshakePullFlag;

    private Integer handshakeCheckFlag;

    private static final long serialVersionUID = 1L;
}