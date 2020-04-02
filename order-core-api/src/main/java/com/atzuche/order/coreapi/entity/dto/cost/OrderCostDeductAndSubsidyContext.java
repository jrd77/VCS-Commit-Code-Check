package com.atzuche.order.coreapi.entity.dto.cost;

import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import lombok.Data;

import java.util.List;

/**
 * 租客订单抵扣和补贴计算公共参数抽取
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
public class OrderCostDeductAndSubsidyContext {


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



    

}
