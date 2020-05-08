package com.atzuche.order.commons.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 节假日类型
 * <p>1-春节，2-清明，3-劳动，4-端午，5-中秋，6-国庆，7-元旦</p>
 *
 * @author pengcheng.fu
 * @date 2020/4/24 10:19
 */

@Getter
public enum HolidayTypeEnum {

    /**
     * 春节
     **/
    HOLIDAY_TYPE_CHUNJIE("1", "春节"),
    /**
     * 清明节
     **/
    HOLIDAY_TYPE_QINGMING("2", "清明节"),
    /**
     * 劳动节
     **/
    HOLIDAY_TYPE_LONGDONG("3", "劳动节"),
    /**
     * 端午节
     **/
    HOLIDAY_TYPE_DUANWU("4", "端午节"),
    /**
     * 中秋节
     **/
    HOLIDAY_TYPE_ZHONGQOU("5", "中秋节"),
    /**
     * 国庆节
     **/
    HOLIDAY_TYPE_GUOQING("6", "国庆节"),
    /**
     * 元旦
     **/
    HOLIDAY_TYPE_YUANDAN("7", "元旦"),

    ;


    private String type;

    private String title;

    HolidayTypeEnum(String type, String title) {
        this.type = type;
        this.title = title;
    }


    /**
     * convert int value to HolidayType
     *
     * @param type int value
     * @return HolidayTypeEnum
     */
    public static HolidayTypeEnum from(String type) {
        HolidayTypeEnum[] types = values();
        for (HolidayTypeEnum t : types) {
            if (StringUtils.equals(type, t.type)) {
                return t;
            }
        }
        throw new RuntimeException("the value of status :" + type + " not supported,please check");
    }

}
