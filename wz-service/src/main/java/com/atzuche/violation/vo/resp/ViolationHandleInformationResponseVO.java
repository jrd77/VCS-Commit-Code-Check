package com.atzuche.violation.vo.resp;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ViolationHandleInformationResponseVO {

    @AutoDocProperty(value = "订单号")
    private String orderNo; // 订单号

    @AutoDocProperty(value = "违章押金")
    private String wzDepositAmt; //违章押金

    @AutoDocProperty("剩余可用违章押金")
    private String shouldReturnDepositAmt;

    @AutoDocProperty(value = "保险理赔金额")
    private String wzInsuranceAmt; //保险理赔金额

    @AutoDocProperty(value = "保险理赔备注")
    private String wzInsuranceRemark; //保险理赔备注

    @AutoDocProperty(value = "其他扣款金额")
    private String otherDeductionAmt; //其他扣款金额

    @AutoDocProperty(value = "其他扣款备注")
    private String otherDeductionRemark; //其他扣款备注

    @AutoDocProperty(value = "协助违章处理费用")
    private String wzFineAmt; //协助违章处理费用

    @AutoDocProperty(value = "协助违章处理费备注")
    private String wzFineRemark; //处理费备注

    @AutoDocProperty(value = "凹凸代办服务费")
    private String wzServiceCostAmt; //凹凸代办服务费

    @AutoDocProperty(value = "服务费备注")
    private String wzServiceCostRemark; //服务费备注

    @AutoDocProperty(value = "不良用车处罚金")
    private String wzDysFineAmt; //不良用车处罚金

    @AutoDocProperty(value = "处罚金备注")
    private String wzDysFineRemark; //处罚金备注

    @AutoDocProperty(value = "停运费")
    private String wzOffStreamCostAmt; //停运费

    @AutoDocProperty(value = "停运费备注")
    private String wzOffStreamCostRemark; //停运费备注

    @AutoDocProperty(value = "违章处理方")
    private String managementMode; //违章处理方

    @AutoDocProperty(value = "违章处理备注")
    private String wzRemarks; //违章处理备注

    @AutoDocProperty(value = "违章退款时间")
    private String violationRefundTime; //违章退款时间

    @AutoDocProperty(value = "违章处理办理完成时间")
    private String wzHandleCompleteTime;

    @AutoDocProperty(value = "租客最晚处理时间")
    private String wzRenterLastTime;

    @AutoDocProperty(value = "平台最晚处理时间")
    private String wzPlatformLastTime;


}
