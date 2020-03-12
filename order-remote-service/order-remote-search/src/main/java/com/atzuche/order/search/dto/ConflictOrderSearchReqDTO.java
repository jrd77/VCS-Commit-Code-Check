package com.atzuche.order.search.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 拉取租期重叠订单信息的请求参数
 *
 * @author pengcheng.fu
 * @date 2020/3/6 14:53
 */

@Data
public class ConflictOrderSearchReqDTO {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 车辆注册号
     */
    private String carNo;

    /**
     * 租期开始时间
     */
    private LocalDateTime rentTime;

    /**
     * 租期截止时间
     */
    private LocalDateTime revertTime;


}
