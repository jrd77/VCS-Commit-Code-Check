package com.atzuche.order.coreapi.entity.dto.cost.req;

import lombok.Data;

import java.util.List;

/**
 * 计算平台优惠券抵扣信息参数
 * <p><font color = red>平台优惠券</font></p>
 *
 * @author pengcheng.fu
 * @date 2020/4/1 11:25
 */
@Data
public class OrderCostPlatformCouponReqDTO {

    /**
     * 优惠券ID
     */
    private String disCouponId;

    /**
     * 城市编码
     */
    private Integer cityCode;

    /**
     * 车辆注册号
     */
    private Integer carNo;

    /**
     * 租客是否是新用户下单
     */
    private Boolean isNew;

    /**
     * 车辆标签(老标签)
     */
    private List<String> labelIds;
}
