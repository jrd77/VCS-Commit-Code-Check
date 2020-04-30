package com.atzuche.order.commons.enums.wz;

import org.apache.commons.lang3.StringUtils;

/**
 * WzCostEnums
 *
 * @author shisong
 * @date 2019/12/31
 */
public enum WzCostEnums {
    WZ_FINE(1,"11240040","协助违章处理费","处理费备注"),
    WZ_DYS_FINE(2,"11240042","不良用车处罚金","服务费备注"),
    WZ_SERVICE_COST(3,"11240041","凹凸代办服务费","处罚金备注"),
    WZ_STOP_COST(4,"11240043","停运费","停运费备注"),
    WZ_OTHER_FINE(5,"11240044","其他扣款","其他扣款备注"),
    INSURANCE_CLAIM(6,"11240045","保险理赔","保险理赔备注"),
    ;

    private Integer type;

    private String code;

    private String desc;

    private String remark;

    public Integer getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getRemark() {
        return remark;
    }

    WzCostEnums(Integer type, String code, String desc,String remark) {
        this.type = type;
        this.code = code;
        this.desc = desc;
        this.remark = remark;
    }

    public static String getCode(Integer type) {
        if(type == null){
            return "";
        }
        WzCostEnums[] values = WzCostEnums.values();
        for (WzCostEnums value : values) {
            if(type.equals(value.getType())){
                return value.getCode();
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
            if(type.equals(value.getType())){
                return value.getDesc();
            }
        }
        return "";
    }

    public static String getDesc(String code) {
        if(StringUtils.isBlank(code)){
            return "";
        }
        WzCostEnums[] values = WzCostEnums.values();
        for (WzCostEnums value : values) {
            if(code.equals(value.getCode())){
                return value.getDesc();
            }
        }
        return "";
    }

    public static Integer getType(String code) {
        if(StringUtils.isBlank(code)){
            return 0;
        }
        WzCostEnums[] values = WzCostEnums.values();
        for (WzCostEnums value : values) {
            if(code.equals(value.getCode())){
                return value.getType();
            }
        }
        return  0;
    }

    public static String getRemark(String code){
        if(StringUtils.isBlank(code)){
            return "";
        }
        WzCostEnums[] values = WzCostEnums.values();
        for (WzCostEnums value : values) {
            if(code.equals(value.getCode())){
                return value.getRemark();
            }
        }
        return  "";
    }
}
