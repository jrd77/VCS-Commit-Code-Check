package com.atzuche.order.coreapi.entity.dto.cost.res;

import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import lombok.Data;

/**
 * 附加驾驶人保险费
 *
 * @author pengcheng.fu
 * @date 2020/3/30 15:28
 */
@Data
public class OrderExtraDriverInsureAmtResDTO {

    /**
     * 附加驾驶人保险费
     */
    private Integer extraDriverInsureAmt;

    /**
     * 附加驾驶人保险费明细
     */
    private RenterOrderCostDetailEntity detail;
}
