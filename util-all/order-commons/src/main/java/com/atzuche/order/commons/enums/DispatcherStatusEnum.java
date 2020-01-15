package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 * 调度状态
 *
 * @author pengcheng.fu
 * @date 2020/1/14 20:29
 */
@Getter
public enum DispatcherStatusEnum {
    /**
     * 未调度
     **/
    NOT_DISPATCH(0, "未调度"),
    /**
     * 调度中
     **/
    DISPATCH_ING(1, "调度中"),
    /**
     * 调度成功
     **/
    DISPATCH_SUCCESS(2, "调度成功"),
    /**
     * 调度失败
     **/
    DISPATCH_FAIL(3, "调度失败");

    private int code;

    private String name;

    DispatcherStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

}
