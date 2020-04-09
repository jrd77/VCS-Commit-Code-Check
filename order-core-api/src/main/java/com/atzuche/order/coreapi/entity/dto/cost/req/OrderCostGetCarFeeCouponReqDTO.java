package com.atzuche.order.coreapi.entity.dto.cost.req;

import lombok.Data;

import java.util.List;

/**
 * 计算送取服务券抵扣信息参数
 * <p><font color = red>送取服务券</font></p>
 *
 * @author pengcheng.fu
 * @date 2020/4/7 14:59
 */
@Data
public class OrderCostGetCarFeeCouponReqDTO {


    /**
     * 取送服务券ID
     */
    private String getCarFreeCouponId;

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
