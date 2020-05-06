package com.atzuche.order.coreapi.entity.dto.cost.res;

import com.atzuche.order.renterorder.vo.RenterOrderIllegalResVO;
import lombok.Data;

/**
 * @author pengcheng.fu
 * @date 2020/4/1 14:12
 */
@Data
public class OrderIllegalDepositAmtResDTO {

    /**
     * 违章押金
     */
    private Integer illegalDepositAmt;

    /**
     * 违章押金明细(收银台使用)
     */
    private RenterOrderIllegalResVO illegalDeposit;
}
