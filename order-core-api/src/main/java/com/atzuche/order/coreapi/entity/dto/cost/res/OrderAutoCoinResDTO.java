package com.atzuche.order.coreapi.entity.dto.cost.res;

import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import lombok.Data;

/**
 * @author pengcheng.fu
 * @date 2020/4/1 14:09
 */
@Data
public class OrderAutoCoinResDTO {

    /**
     * 补贴金额
     */
    private Integer subsidyAmt;

    /**
     * 补贴明细
     */
    private RenterOrderSubsidyDetailDTO subsidyDetail;
}
