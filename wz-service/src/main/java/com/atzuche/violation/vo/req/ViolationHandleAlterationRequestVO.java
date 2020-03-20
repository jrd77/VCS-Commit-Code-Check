package com.atzuche.violation.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ViolationHandleAlterationRequestVO {

    @AutoDocProperty(value = "订单号")
    private String orderNo; // 订单号

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

    @AutoDocProperty(value = "违章处理方,1:租客自行办理违章 2:凹凸代为办理违章 3:车主自行办理 4:无数据")
    private String managementMode; //违章处理方

    @AutoDocProperty(value = "违章处理备注")
    private String wzRemarks; //违章处理备注

    @AutoDocProperty(value = "违章处理办理完成时间,格式yyyy-MM-dd HH:mm:ss")
    private String wzHandleCompleteTime;

    @AutoDocProperty(value = "租客最晚处理时间,yyyy-MM-dd HH:mm:ss")
    private String wzRenterLastTime;

    @AutoDocProperty(value = "平台最晚处理时间,yyyy-MM-dd HH:mm:ss")
    private String wzPlatformLastTime;


}
