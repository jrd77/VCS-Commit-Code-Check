package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderAccountDetailRespDTO {

    @AutoDocProperty(value="订单状态")
    public OrderStatusDTO orderStatusDTO;
    @AutoDocProperty(value="租客押金减免信息")
    public RenterDepositDetailDTO renterDepositDetailDTO;
    @AutoDocProperty(value="租客车辆押金详情")
    public List<AccountRenterDepositDetailDTO> accountRenterDepositDetailDTOList;
    @AutoDocProperty(value="租客车辆押金费用")
    public AccountRenterDepositDTO accountRenterDepositDTO;
    @AutoDocProperty(value="租客暂扣费用")
    public AccountRenterDetainCostDTO accountRenterDetainCostDTO;
    @AutoDocProperty(value="租客暂扣费用明细")
    public List<AccountRenterDetainDetailDTO> accountRenterDetainDetailDTOList;
    @AutoDocProperty(value="租客租车费用明细")
    public List<AccountRenterCostDetailDTO> accountRenterCostDetailDTOS;

}
