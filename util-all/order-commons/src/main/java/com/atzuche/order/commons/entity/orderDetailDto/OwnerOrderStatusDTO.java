package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

@Data
public class OwnerOrderStatusDTO {
    /**
     * 车主子单状态，1-待补付,2-修改待确认,3-进行中,4-已完结,5-已结束
     */
    private Integer childStatus;
    /**
     * 是否取消 0-正常，1-取消
     */
    private Integer isCancel;
}
