package com.atzuche.order.commons.enums.cashcode;

import lombok.Getter;

/*
 * @Author ZhangBin
 * @Date 2020/1/15 21:08
 * @Description: 后台管理补贴明细表费用编码枚举类
 * 收费序号,平台从30开头 平台 第一位：1，第二位：1正2负      1正2负 3可正可负   ->0可正可负，第三位：押金类型，第四位：分模块（区域），后4位是编码
 **/
@Getter
public enum ConsoleCashCodeEnum {
	//第一区域块 看第4位，从1开始。  后4位从0001开始
    TIME_OUT("31010001","车主超时费用"),
    CAR_WASH("31010002","车主车辆清洗费"),
    DLAY_WAIT("31010003","车主延误等待费"),
    STOP_CAR("31010004","车主停车费"),
    EXTRA_MILEAGE("31010005","车主超里程"),
    MODIFY_ADDR_TIME("31010006","车主临时修改订单地址时间"),
    OIL_FEE("31010007","车主油费"),
    
  //第二区域块 看第4位，从2开始。  后4位从0001开始
    //根据type来分
    RENTER_TIME_OUT("31020001","租客超时费用"),
    RENTER_CAR_WASH("31020002","租客车辆清洗费"),
    RENTER_DLAY_WAIT("31020003","租客延误等待费"),
    RENTER_STOP_CAR("31020004","租客停车费"),
    RENTER_EXTRA_MILEAGE("31020005","租客超里程"),
    RENTER_MODIFY_ADDR_TIME("31020006","租客临时修改订单地址时间"),
    RENTER_OIL_FEE("31020007","租客油费")
    
    //补贴
    
    //第三区域块 看第4位，从3开始。  后4位从0001开始    后面是以此类推，确保费用编码不能重复。
    
    
    ;

    /**
     * 费用编码
     */
    private String cashNo;

    /**
     * 费用描述
     */
    private String txt;

    ConsoleCashCodeEnum(String cashNo, String txt) {
        this.cashNo = cashNo;
        this.txt = txt;
    }
}
