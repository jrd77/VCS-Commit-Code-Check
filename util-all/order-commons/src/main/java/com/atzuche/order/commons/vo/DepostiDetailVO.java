package com.atzuche.order.commons.vo;

import com.autoyol.doc.annotation.AutoDocProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/13 12:53 下午
 **/
@Data
@ToString
public class DepostiDetailVO {
    /**
     * 支付状态
     */
    @AutoDocProperty(value = "支付状态")
    private Integer payStatus;
    /**
     * 支付时间
     */
    @AutoDocProperty(value = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;
    /**
     * 结算状态
     */
    @AutoDocProperty(value = "结算状态")
    private Integer settleStatus;
    /**
     * 结算时间
     */
    @AutoDocProperty(value = "结算时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date settleTime;
    /**
     * 应付押金总额
     */
    @AutoDocProperty(value = "应付押金总额")
    private Integer yingfuDepositAmt;
    /**
     * 实付押金总金额
     */
    @AutoDocProperty(value = "实付押金总金额")
    private Integer shifuDepositAmt;
    /**
     * 预授权金额
     */
    @AutoDocProperty(value = "预授权金额")
    private Integer authorizeDepositAmt;
    /**
     * 信用支付金额
     */
    @AutoDocProperty(value = "信用支付金额")
    private Integer creditPayAmt;
    /**
     * 剩余信用支付金额
     */
    @AutoDocProperty(value = "剩余信用支付金额")
    private Integer surplusCreditPayAmt;
    /**
     * 剩余押金总额
     */
    @AutoDocProperty(value = "剩余押金总额")
    private Integer surplusDepositAmt;
    /**
     * 剩余预授权金额
     */
    @AutoDocProperty(value = "剩余预授权金额")
    private Integer surplusAuthorizeDepositAmt;
    /**
     * 免押金额
     */
    @AutoDocProperty(value = "免押金额")
    private Integer reductionAmt;
    /**
     * 开启免押
     */
    @AutoDocProperty(value = "开启免押")
    private Integer isFreeDeposit;
    /**
     * 免押方式(1:绑卡减免,2:芝麻减免,3:消费)
     */
    @AutoDocProperty(value = "免押方式(1:绑卡减免,2:芝麻减免,3:消费")
    private Integer freeDepositType;
}
