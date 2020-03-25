package com.atzuche.violation.enums;

/**
 * @author 胡春林
 * 燃料类型 1：92号汽油、2：95号汽油、3：0号柴油、4：纯电动、5: 98号汽油
 */
public enum OilCostTypeEnum {


    NO92_GASOLINE(1, "92号汽油"),
    NO95_GASOLINE(2, "95号汽油"),
    NO0_GASOLINE(3, "0号柴油"),
    NO4_GASOLINE(4, "纯电动"),
    NO98_GASOLINE(5, "98号汽油"),
    ;

    private Integer value;
    private String name;

    OilCostTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static String getOilCostType(Integer value) {
        for (OilCostTypeEnum typeEnum : values()) {
            if (typeEnum.getValue().intValue() == value.intValue()) {
                return typeEnum.getName();
            }
        }
        return "没有合适的燃油类型";
    }

}
