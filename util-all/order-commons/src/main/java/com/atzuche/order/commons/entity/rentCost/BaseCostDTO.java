package com.atzuche.order.commons.entity.rentCost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BaseCostDTO {

    @AutoDocProperty("租客租金")
    public Integer renterAmt = 0;
    @AutoDocProperty("手续费")
    public Integer serviceFee = 0;
    @AutoDocProperty("基础保障费")
    public Integer basicGuaranteeFee = 0;
    @AutoDocProperty("全面保障服务费")
    public Integer allGuaranteeFee = 0;
    @AutoDocProperty("附加驾驶员险")
    public Integer driverInsurance = 0;
    @AutoDocProperty("配送费用")
    public Integer distributionCost = 0;
    @AutoDocProperty("违约罚金")
    public Integer penaltyBreachContract = 0;
    @AutoDocProperty("超里程费用")
    public Integer extraMileageFee = 0;
    @AutoDocProperty("油费")
    public Integer oilFee = 0;
    @AutoDocProperty("租客需支付给平台的费用")
    public Integer payToPlatFormFee = 0;
    @AutoDocProperty("租客车主互相调价")
    public Integer renterOWnerAdjustmentFee = 0;
}
