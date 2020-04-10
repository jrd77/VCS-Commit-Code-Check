package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 提前延后时间计算返回结果
 *
 * @author pengcheng.fu
 * @date 2019/12/31 17:11
 */

@Data
public class CarRentTimeRangeDTO {

    @AutoDocProperty(value = "提前分钟数")
    private Integer getMinutes;

    @AutoDocProperty(value = "延后分钟数")
    private Integer returnMinutes;

    @AutoDocProperty(value = "提前开始租期")
    private LocalDateTime advanceStartDate;

    @AutoDocProperty(value = "延后结束租期")
    private LocalDateTime delayEndDate;




}
