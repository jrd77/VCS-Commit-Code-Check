package com.atzuche.order.commons.entity.rentCost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class RenterCostDetailDTO {
    @AutoDocProperty("费用合计")
    public CostStatisticsDTO costStatisticsDTO;

    @AutoDocProperty("租车费用")
    public RentCarCostDTO rentCarCostDTO;

    @AutoDocProperty("车辆押金")
    public CarDepositDTO carDepositDTO;

    @AutoDocProperty("违章押金")
    public WzDepositDTO wzDepositDTO;

    @AutoDocProperty("租车费用结算后补付")
    public SettleMakeUpDTO settleMakeUpDTO;
}
