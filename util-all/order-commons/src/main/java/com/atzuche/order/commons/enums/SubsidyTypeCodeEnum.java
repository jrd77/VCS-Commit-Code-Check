package com.atzuche.order.commons.enums;

import lombok.Getter;

/*
 * @Author ZhangBin
 * @Date 2019/12/25 20:31
 * @Description: 补贴类型 编码
 *
 **/
@Getter
public enum SubsidyTypeCodeEnum {
    /**
     * 补贴类型,取还车费用(用于像送取服务券抵扣等)
     */
    GET_RETURN_CAR("0","取还车费用"),
    /**
     * 补贴类型,取车费用
     */
    GET_CAR("1","取车费用"),
    /**
     * 补贴类型,还车费用
     */
    RETURN_CAR("2","还车费用"),
    /**
     * 补贴类型,租金
     */
    RENT_AMT("3","租金"),
    
    INSURE_AMT("4","平台保障费"),
    
    ABATEMENT_INSURE("5","全面保障费"),
    
    RENT_COST_AMT("6","租车费用")

    ;

    private String code;
    private String desc;

    SubsidyTypeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
