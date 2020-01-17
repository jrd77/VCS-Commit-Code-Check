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
	OIL_FEE("21010000","车主油费"),
    TIME_OUT("21010001","车主超时费用"),
    CAR_WASH("21010002","车主车辆清洗费"),
    DLAY_WAIT("21010003","车主延误等待费"),
    STOP_CAR("21010004","车主停车费"),
    EXTRA_MILEAGE("21010005","车主超里程"),
    MODIFY_ADDR_TIME("21010006","车主临时修改订单地址时间"),
    
    
    //根据type来分
    RENTER_OIL_FEE("11010000","租客油费"),
    RENTER_TIME_OUT("11010001","租客超时费用"),
    RENTER_CAR_WASH("11010002","租客车辆清洗费"),
    RENTER_DLAY_WAIT("11010003","租客延误等待费"),
    RENTER_STOP_CAR("11010004","租客停车费"),
    RENTER_EXTRA_MILEAGE("11010005","租客超里程"),
    RENTER_MODIFY_ADDR_TIME("11010006","租客临时修改订单地址时间")
    
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
