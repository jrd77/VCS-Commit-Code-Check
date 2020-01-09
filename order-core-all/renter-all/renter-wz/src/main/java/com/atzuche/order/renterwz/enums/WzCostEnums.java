package com.atzuche.order.renterwz.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * WzCostEnums
 *
 * @author shisong
 * @date 2019/12/31
 */
public enum WzCostEnums {
    WZ_FINE(1,"chuLiFei","协助违章处理费"),
    WZ_DYS_FINE(2,"chuFaJin","不良用车处罚金"),
    WZ_SERVICE_COST(3,"fuWuFei","凹凸代办服务费"),
    WZ_STOP_COST(4,"tingYunFei","停运费"),
    WZ_OTHER_FINE(5,"qiTaFei","其他扣款"),
    INSURANCE_CLAIM(6,"baoXianLiPei","保险理赔"),
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
}
