package com.atzuche.order.coreapi.entity.dto.cost.res;

import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import lombok.Data;

import java.util.List;

/**
 * 长租订单取还车服务补贴信息
 *
 * @author pengcheng.fu
 * @date 2020/4/1 14:19
 */
@Data
public class LongOrderGetAndReturnCarCostSubsidyResDTO {

    /**
     * 取车费用补贴金额
     */
    private Integer getCarSubsidyAmt;

    /**
     * 还车费用补贴金额
     */
    private Integer returnCarSubsidyAmt;

    /**
     * 补贴明细
     */
    private List<RenterOrderSubsidyDetailDTO> subsidyDetails;

}
