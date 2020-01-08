package com.atzuche.order.delivery.enums;

/**
 * @author 胡春林
 * 车辆类型
 */
public enum  CarTypeEnum {

    TRADING_USE(20, "托管车辆-交易用"),
    WORK_USE(25, "托管车辆-工作用"),
    MANAGED_CAR(30, "期托管车"),
    ESCROW_VEHICLE(35, "代管车辆"),
            ;

    private Integer value;

    private String name;

    CarTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    /**
     * 是否是托管车
     * @param value
     * @return
     */
    public static boolean isCarType(Integer value) {
        for (CarTypeEnum carTypeEnum : values()) {
            if (carTypeEnum.getValue().intValue() == value.intValue()) {
                return true;
            }
        }
        return false;
    }


}
