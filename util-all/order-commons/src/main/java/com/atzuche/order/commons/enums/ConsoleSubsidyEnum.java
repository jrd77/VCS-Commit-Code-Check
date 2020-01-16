package com.atzuche.order.commons.enums;

import lombok.Getter;

/*
 * @Author ZhangBin
 * @Date 2020/1/15 21:08
 * @Description: 后台管理补贴明细表费用编码枚举类
 *
 **/
@Getter
public enum ConsoleSubsidyEnum {

    TIME_OUT_SUBSIDY("21010001","超时费用"),
    car_wash_subsidy("21010002","车辆清洗费"),
    dlay_wait_subsidy("21010003","延误等待费"),
    stop_cat_subsidy("21010004","停车费"),
    oil_amt_subsidy("21010005","油费补贴")

    //补贴
    ;

    /**
     * 费用编码
     */
    private String cashNo;

    /**
     * 费用描述
     */
    private String txt;

    ConsoleSubsidyEnum(String cashNo, String txt) {
        this.cashNo = cashNo;
        this.txt = txt;
    }
}
