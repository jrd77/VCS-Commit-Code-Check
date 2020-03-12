package com.atzuche.order.commons.entity.rentCost;

import lombok.Data;

@Data
public class DeductionRentAmtDTO {
    //预计车辆押金抵扣的租车费用
    private int carDepositDeductionAmt = 0;
    //预计违章押金抵扣的租车费用
    private int wzDepositDeductionAmt = 0;
}
