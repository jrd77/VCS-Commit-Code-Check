package com.atzuche.order.transport.vo.delivery;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.vo.res.delivery.RenterOrderDeliveryRepVO;
import lombok.*;

import java.util.List;

/**
 * @author 胡春林
 * 基础参数
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryCarFeeBaseParamsVO {

    /**
     * 基础参数
     */
    private CostBaseDTO costBaseDTO;
    /**
     * 配送订单数据
     */
    private List<RenterOrderDeliveryRepVO> renterOrderDeliveryRepVOList;

    /**
     * 租客商品数据
     */
    private RenterGoodsDetailDTO renterGoodsDetailDTO;
}
