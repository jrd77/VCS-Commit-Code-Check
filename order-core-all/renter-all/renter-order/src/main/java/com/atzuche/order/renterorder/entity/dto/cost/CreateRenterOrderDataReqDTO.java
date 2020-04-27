package com.atzuche.order.renterorder.entity.dto.cost;

import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.renterorder.entity.OwnerCouponLongEntity;
import com.atzuche.order.renterorder.entity.RenterAdditionalDriverEntity;
import com.atzuche.order.renterorder.entity.RenterDepositDetailEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import lombok.Data;

import java.util.List;

/**
 * 下单落库数据组合
 *
 * @author pengcheng.fu
 * @date 2020/4/10 18:30
 */

@Data
public class CreateRenterOrderDataReqDTO {

    /**
     * 租客订单信息
     */
    private RenterOrderEntity renterOrderEntity;

    /**
     * 租客订单费用详情
     */
    private RenterOrderCostRespDTO RenterOrderCostRespDTO;

    /**
     * 附加驾驶人列表
     */
    private List<RenterAdditionalDriverEntity> renterAdditionalDriverEntities;

    /**
     * 优惠券列表
     */
    private List<OrderCouponDTO> orderCouponList;

    /**
     * 车辆押金详情
     */
    private RenterDepositDetailEntity renterDepositDetailEntity;

    /**
     * 长租订单抵扣信息
     */
    private OwnerCouponLongEntity ownerCouponLongEntity;


}
