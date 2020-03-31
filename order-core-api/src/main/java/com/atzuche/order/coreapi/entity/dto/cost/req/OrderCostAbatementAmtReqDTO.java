package com.atzuche.order.coreapi.entity.dto.cost.req;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 计算全面保障费参数
 *
 * @author pengcheng.fu
 * @date 2020/3/27 16:45
 */
@Data
public class OrderCostAbatementAmtReqDTO {


    /**
     * 提前时间（分钟数）
     */
    private Integer getCarBeforeTime;
    /**
     * 延后时间（分钟数）
     */
    private Integer returnCarAfterTime;
    /**
     * 车辆指导价格
     */
    private Integer guidPrice;
    /**
     * 保费计算用购置价（保费购置价为空取车辆指导价算）
     */
    private Integer inmsrp;
    /**
     * 驾驶证初次领证日期
     */
    private LocalDate certificationTime;
    /**
     * 车辆标签
     */
    private List<String> carLabelIds;
    /**
     * 是否计算全面保障费
     */
    private Boolean isAbatement;

}
