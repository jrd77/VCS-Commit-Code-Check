package com.atzuche.order.coreapi.entity.dto;

import lombok.Data;

/**
 * 订单是否进调度
 *
 * @author pengcheng.fu
 * @date 2020/3/2514:17
 */
@Data
public class CheckCarDispatchResDTO {

    /**
     * 是否进入调度
     */
    private Boolean isDispatch;

    /**
     * 不进调度的原因
     */
    private String noDispatchReason;

    public CheckCarDispatchResDTO() {
    }

    public CheckCarDispatchResDTO(Boolean isDispatch) {
        this.isDispatch = isDispatch;
    }

    public CheckCarDispatchResDTO(Boolean isDispatch, String noDispatchReason) {
        this.isDispatch = isDispatch;
        this.noDispatchReason = noDispatchReason;
    }
}
