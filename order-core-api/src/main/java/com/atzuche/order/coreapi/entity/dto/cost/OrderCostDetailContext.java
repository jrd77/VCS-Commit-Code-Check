package com.atzuche.order.coreapi.entity.dto.cost;

import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订单费用明细(订单费用以及补贴等)
 * <p><font color=red>注:迭代处理租金和取还车服务费信息</font></p>
 *
 * <p>1. 送取服务券抵扣信息及补贴明细</p>
 * <p>2. 车主券抵扣信息及补贴明细</p>
 * <p>3. 限时红包补贴明细</p>
 * <p>4. 平台优惠券抵扣及补贴明细</p>
 * <p>5. 凹凸币补贴明细</p>
 *
 * <p>6. 长租订单租金抵扣信息及补贴明细</p>
 * <p>7. 长租订单取还车服务费补贴明细</p>
 *
 * @author pengcheng.fu
 * @date 2020/4/1 10:55
 */

@Data
public class OrderCostDetailContext implements Serializable {

    private static final long serialVersionUID = -2038429673649358854L;

    /**
     * 原始租金
     */
    private Integer originalRentAmt;
    /**
     * 取车服务费(取送服务券包含超运能溢价)
     */
    private Integer srvGetCost;
    /**
     * 还车服务费(取送服务券包含超运能溢价)
     */
    private Integer srvReturnCost;
    /**
     * 剩余租金
     */
    private Integer surplusRentAmt;
    /**
     * 剩余取车服务费(取送服务券包含超运能溢价)
     */
    private Integer surplusSrvGetCost;
    /**
     * 剩余还车服务费(取送服务券包含超运能溢价)
     */
    private Integer surplusSrvReturnCost;


    /**
     * 费用明细
     */
    private List<RenterOrderCostDetailEntity> costDetails;
    /**
     * 优惠券列表
     */
    private List<OrderCouponDTO> coupons;
    /**
     * 租客订单补贴明细
     */
    private List<RenterOrderSubsidyDetailDTO> subsidyDetails;

}
