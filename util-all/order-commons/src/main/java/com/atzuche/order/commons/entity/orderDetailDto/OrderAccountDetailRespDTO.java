package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderAccountDetailRespDTO {
    @AutoDocProperty(value="租客押金详情",required=true)
    public List<AccountRenterDepositDetailDTO> accountRenterDepositDetailDTOList;
    @AutoDocProperty(value="租客押金费用",required=true)
    public AccountRenterDepositDTO accountRenterDepositDTO;
    @AutoDocProperty(value="租客暂扣费用",required=true)
    public AccountRenterDetainCostDTO accountRenterDetainCostDTO;
    @AutoDocProperty(value="租客暂扣费用明细",required=true)
    public List<AccountRenterDetainDetailDTO> accountRenterDetainDetailDTOList;
}
