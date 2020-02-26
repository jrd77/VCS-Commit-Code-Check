package com.atzuche.order.detain.vo;

import com.atzuche.order.commons.enums.detain.DetailSourceEnum;
import lombok.Data;

/**
 * 租客订单暂扣表
 */
@Data
public class RenterDetainVO {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 租客订单号
     */
    private String renterOrderNo;

    /**
     * 暂扣部门ID
     */
    private Integer deptId;
    /**
     * 暂扣部门
     */
    private String deptName;

    /**
     * 暂扣事件类型
     */
    private DetailSourceEnum eventType;

    /**
     * 事件描述
     */
    private String eventDesc;
    /**
     * 备注
     */
    private String remake;

}
