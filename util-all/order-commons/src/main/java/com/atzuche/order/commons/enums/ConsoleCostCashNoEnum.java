package com.atzuche.order.commons.enums;

import lombok.Getter;

/*
 * @Author ZhangBin
 * @Date 2020/1/15 21:08
 * @Description: 后台管理补贴明细表费用编码枚举类
 *
 **/
@Getter
public enum ConsoleCostCashNoEnum {

    TIME_OUT("21010001","超时费用"),
    CAR_WASH("21010002","车辆清洗费"),
    DLAY_WAIT("21010003","延误等待费"),
    STOP_CAR("21010004","停车费"),
    EXTRA_MILEAGE("21010005","超里程"),
    MODIFY_ADDR_TIME("21010006","临时修改订单地址时间")

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

    ConsoleCostCashNoEnum(String cashNo, String txt) {
        this.cashNo = cashNo;
        this.txt = txt;
    }
}
