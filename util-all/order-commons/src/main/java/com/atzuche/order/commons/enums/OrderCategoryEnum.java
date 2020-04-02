package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 * 订单类型
 *
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/9 2:19 下午
 **/
@Getter
public enum OrderCategoryEnum {
    /**
     * 订单类型-短租订单
     **/
    SHORT_ORDER("1", "短租订单"),
    /**
     * 订单状态-套餐订单
     **/
    PACKAGE_ORDER("2", "套餐订单"),
    /**
     * 订单状态-长租订单
     **/
    LONG_ORDER("3", "长租订单");


    private String category;
    private String desc;

    /**
     * constructor
     *
     * @param category category value
     * @param desc     category description
     */
    OrderCategoryEnum(String category, String desc) {
        this.category = category;
        this.desc = desc;
    }

    /**
     * convert int value to OrderStatus
     *
     * @param category int value
     * @return OrderCategoryEnum
     */
    public static OrderCategoryEnum from(String category) {
        OrderCategoryEnum[] statuses = values();
        for (OrderCategoryEnum s : statuses) {
            if (s.category.equals(category)) {
                return s;
            }
        }
        throw new RuntimeException("the value of category :" + category + " not supported,please check");
    }

}
