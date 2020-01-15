package com.atzuche.order.commons.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 平台取消原因
 *
 * @author pengcheng.fu
 * @date 2020/1/15 20:03
 */

@Getter
public enum PlatformCancelReasonEnum {

    /**
     * 租客审核不通过（风控）
     */
    RENTER_AUDIT_NO_PASS("0", "租客审核不通过（风控）"),
    /**
     * 车载设备有问题
     */
    VEHICLE("1", "车载设备有问题"),
    /**
     * 车辆已下架
     */
    CAR_LOWER("2", "车辆已下架"),
    /**
     * 租客取消不产生违约金（代步车）
     */
    RENTER_CANCEL_NO_FINE("3", "租客取消不产生违约金（代步车）"),
    /**
     * 测试订单（交易）
     */
    TEST("4", "测试订单（交易）"),
    /**
     * 机械故障结束订单（交易）
     */
    MECHANICAL_FAILURE("5", "机械故障结束订单（交易）"),
    /**
     * 调度成功重新成单（交易）
     */
    DISPATCH_SUC_NEW_ORDER("6", "调度成功重新成单（交易）");

    private String code;

    private String name;

    PlatformCancelReasonEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * convert int value to PlateformCancelReasonEnum
     *
     * @param code int value
     * @return PlateformCancelReasonEnum
     */
    public PlatformCancelReasonEnum from(String code) {
        PlatformCancelReasonEnum[] reasonEnums = values();
        for (PlatformCancelReasonEnum reason : reasonEnums) {
            if (StringUtils.equals(reason.code, code)) {
                return reason;
            }
        }
        throw new RuntimeException("the value of code :" + code + " not supported,please check");
    }
}
