package com.atzuche.order.commons.vo.res;

import com.atzuche.order.commons.entity.orderDetailDto.*;
import lombok.Data;

import java.util.List;

@Data
public class PaymentRespVO {
    private List<CashierResVO> cashierResVOList;
    private List<OfflineRefundApplyDTO> offlineRefundApplyDTOList;
    private List<AccountVirtualPayDetailDTO> accountVirtualPayDetailDTOList;
    private List<CashierRefundApplyDTO> CashierRefundApplyDTOList;
    private List<AccountRenterCostDetailDTO> accountRenterCostDetailDTOList;
    private OrderStatusDTO orderStatusDTO;
}
