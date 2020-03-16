package com.atzuche.order.commons.entity.rentCost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class CarDepositDTO {

    @AutoDocProperty("费用合计")
    public CostStatisticsDTO costStatisticsDTO;

    @AutoDocProperty("车辆押金")
    public Integer carDeposit = 0;
    @AutoDocProperty("平台任务减免")
    public Integer platformTaskRelief = 0;
    @AutoDocProperty("预计抵扣租车费用")
    public Integer expDeductionRentAmt = 0;
    @AutoDocProperty("抵扣历史欠款")
    public Integer DeductionRentHistoricalAmt = 0;
}
