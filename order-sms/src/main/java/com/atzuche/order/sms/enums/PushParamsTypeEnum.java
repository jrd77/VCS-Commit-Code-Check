package com.atzuche.order.sms.enums;

/**
 * @author 胡春林
 */
public enum  PushParamsTypeEnum {

    /*##################################################################PUSH信息模版参数替换（新订单）###################################################################################################################*/
    CAR_BRAND_NAME("carBrandTxt", "carBrandName"),
    CAR_TYPE_NAME("carTypeTxt", "carTypeName"),
    CAR_PLATE_NUM("carPlateNum", "plateNum"),
    LEFT_HOURS("leftHours", "leftHours"),
    ;


    /**
     * 目标值
     */
    private String value;

    /**
     * 需要替换得值
     */
    private String name;

    PushParamsTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    /**
     * 获取push消息模版
     * @param name
     * @return
     */
    public static String getFeildValue(String name) {
        for (PushParamsTypeEnum pushParamsTypeEnum : values()) {
            if (pushParamsTypeEnum.getName().equals(name)) {
                return pushParamsTypeEnum.getValue();
            }
        }
        return null;
    }

    public static String getFeildName(String value) {
        for (PushParamsTypeEnum pushParamsTypeEnum : values()) {
            if (pushParamsTypeEnum.getValue().equals(value)) {
                return pushParamsTypeEnum.getName();
            }
        }
        return null;
    }
}
