package com.atzuche.order.coreapi.entity.dto.cost.res;

import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import lombok.Data;

/**
 *
 * 基础保障费折扣信息
 *
 * @author pengcheng.fu
 * @date 2020/3/31 15:26
 */
@Data
public class OrderInsurAmtDeductionResDTO {

    /**
     * 补贴金额
     */
    private Integer subsidyAmt;

    /**
     * 补贴明细
     */
    private RenterOrderSubsidyDetailDTO subsidyDetail;

}
