package com.atzuche.order.renterwz.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * WzCostEnums
 *
 * @author shisong
 * @date 2019/12/31
 */
public enum WzCostEnums {
    WZ_FINE(1,"100040","协助违章处理费","处理费备注"),
    WZ_DYS_FINE(2,"100042","不良用车处罚金","服务费备注"),
    WZ_SERVICE_COST(3,"100041","凹凸代办服务费","处罚金备注"),
    WZ_STOP_COST(4,"100043","停运费","停运费备注"),
    WZ_OTHER_FINE(5,"100044","其他扣款","其他扣款备注"),
    INSURANCE_CLAIM(6,"100045","保险理赔","保险理赔备注"),
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
