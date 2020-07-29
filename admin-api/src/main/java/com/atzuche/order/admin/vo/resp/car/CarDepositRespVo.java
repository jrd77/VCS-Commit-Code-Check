package com.atzuche.order.admin.vo.resp.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class CarDepositRespVo {
    @AutoDocProperty("车辆押金")
    private Integer carDepositMonty;

    @AutoDocProperty("平台任务减免")
    private Integer originalTotalAmt;

    @AutoDocProperty("应收")
    private Integer receivableMonty;

    @AutoDocProperty(value="支付方式")
    private String  payType;

    @AutoDocProperty(value="支付类型")
    private String depositType;

    @AutoDocProperty("减免金额")
    private String reliefAmtStr;
    @AutoDocProperty("支付时间")
    private String payDateStr;

    @AutoDocProperty("支付状态")
    private String payStatusStr;

    @AutoDocProperty(value="剩余可用车辆押金")
    private Integer surplusDepositAmt;

    @AutoDocProperty("实际抵扣租车费用")
    private Integer realDeductionRentCarAmt;

    @AutoDocProperty("预计抵扣租车费用")
    private Integer expDeductionRentCarAmt;

    @AutoDocProperty(value = "预计与实际标记：1:预计，2实际")
    private int expOrActFlag;


    @AutoDocProperty("结算时抵扣的历史欠款")
    private Integer deductionHistoryAmt;



    @AutoDocProperty("车辆押金预计结算时间")
    private String expSettleTime;

    @AutoDocProperty("车辆押金实际结算时间")
    private String actSettleTime;

    @AutoDocProperty("实际已暂扣金额")
    private Integer actDetainAmt;

    @AutoDocProperty("暂扣状态（最新暂扣信息）")
    private String actDetainStatus;

    @AutoDocProperty("扣款时间（最新暂扣信息）")
    private String actDetainTime;


    @AutoDocProperty(value = "风控-暂扣车辆押金 0-否 1-是")
    private String fkDetainFlag;

    @AutoDocProperty(value = "风控-暂扣原因")
    private String fkDetainReason;

    @AutoDocProperty(value = "交易-暂扣车辆押金 0-否 1-是")
    private String jyDetainFlag;

    @AutoDocProperty(value = "交易-暂扣原因")
    private String jyDetainReason;

    @AutoDocProperty(value = "理赔-暂扣车辆押金 0-否 1-是")
    private String lpDetainFlag;

    @AutoDocProperty(value = "理赔-暂扣原因")
    private String lpDetainReason;




}
