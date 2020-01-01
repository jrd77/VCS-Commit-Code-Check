package com.atzuche.order.renterwz.enums;

/**
 * WzCostEnums
 *
 * @author shisong
 * @date 2019/12/31
 */
public enum WzCostEnums {
    WZ_FINE(1,"","协助违章处理费"),
    WZ_DYS_FINE(2,"","不良用车处罚金"),
    WZ_SERVICE_COST(3,"","凹凸代办服务费"),
    WZ_STOP_COST(4,"","停运费"),
    WZ_OTHER_FINE(5,"","其他扣款"),
    ;

    private Integer type;

    private String code;

    private String desc;

    public Integer getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    WzCostEnums(Integer type, String code, String desc) {
        this.type = type;
        this.code = code;
        this.desc = desc;
    }

    public static String getCode(Integer type) {
        if(type == null){
            return "";
        }
        WzCostEnums[] values = WzCostEnums.values();
        for (WzCostEnums value : values) {
            if(type.equals(value.type)){
                return value.code;
            }
        }
        return "";
    }

    public static String getDesc(Integer type) {
        if(type == null){
            return "";
        }
        WzCostEnums[] values = WzCostEnums.values();
        for (WzCostEnums value : values) {
            if(type.equals(value.type)){
                return value.desc;
            }
        }
        return "";
    }
}
