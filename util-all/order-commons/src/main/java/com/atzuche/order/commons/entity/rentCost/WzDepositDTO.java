package com.atzuche.order.commons.entity.rentCost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class WzDepositDTO {
    @AutoDocProperty("费用合计")
    public CostStatisticsDTO costStatisticsDTO;

    @AutoDocProperty(value = "违章押金")
    public Integer wzDepositAmt=0;
    @AutoDocProperty(value = "协助违章处理费")
    public Integer wzFineAmt=0;
    @AutoDocProperty(value = "凹凸代办服务费")
    public Integer wzServiceCostAmt=0;
    @AutoDocProperty(value = "不良用车处罚金")
    public Integer wzDysFineAmt=0;
    @AutoDocProperty(value = "停运费")
    public Integer wzStopCostAmt=0;
    @AutoDocProperty(value = "其他扣款")
    public Integer wzOtherAmt=0;
    @AutoDocProperty(value = "保险理赔")
    public Integer wzInsuranceAmt=0;
    @AutoDocProperty(value = "抵扣的租车费用")
    public Integer expDeductionRentCarAmt = 0;
    @AutoDocProperty("预计和实际抵扣租车费用标志 1：预计、2实际")
    public Integer expAndActFlg;
    @AutoDocProperty("抵扣历史欠款")
    public Integer deductionRentHistoricalAmt = 0;

}
