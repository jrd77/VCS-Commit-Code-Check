package com.atzuche.order.coreapi.entity.dto.cost.res;

import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import lombok.Data;

import java.util.List;

/**
 * 长租订单车主券抵扣租金信息
 *
 * @author pengcheng.fu
 * @date 2020/4/114:08
 */

@Data
public class LongOrderOwnerCouponResDTO {

    /**
     * 补贴金额
     */
    private Integer subsidyAmt;

    /**
     * 补贴明细
     */
    private List<RenterOrderSubsidyDetailDTO> subsidyDetails;
}
