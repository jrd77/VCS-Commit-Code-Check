package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

@Data
public class renterOrderChangeApplyStatusDTO {
    /**
     * 是否修改起租时间 0-否，1-是
     */
    private Integer rentTimeFlag;
    /**
     * 申请类型 1-租期修改，2-取还车地址修改，3-租期地址修改）
     */
    private Integer applyType;


}
