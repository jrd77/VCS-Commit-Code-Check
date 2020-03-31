package com.atzuche.order.coreapi.entity.dto.cost.req;

import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import lombok.Data;

import java.util.List;

/**
 * 计算租金参数
 *
 * @author pengcheng.fu
 * @date 2020/3/27 16:41
 */

@Data
public class OrderCostRentAmtReqDTO {


    /**
     * 一天一价
     */
    private List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList;
}
