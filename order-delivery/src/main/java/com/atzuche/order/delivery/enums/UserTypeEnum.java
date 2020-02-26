package com.atzuche.order.delivery.enums;

/**
 * @author 胡春林
 * 用户类型
 */
public enum UserTypeEnum {

    RENTER_TYPE(1, "租客"),
    OWNER_TYPE(2, "车主");

    private Integer value;

    private String name;

    UserTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static boolean isUserType(Integer value) {
        for (UserTypeEnum typeEnum : values()) {
            if (typeEnum.getValue().intValue() == value.intValue()) {
                return true;
            }
        }
        return false;
    }
}
