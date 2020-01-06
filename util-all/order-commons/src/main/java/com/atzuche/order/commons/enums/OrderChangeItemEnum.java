package com.atzuche.order.commons.enums;

import lombok.Getter;
/**
 * 修改项目
 */
@Getter
public enum OrderChangeItemEnum {

   
	MODIFY_ABATEMENT("1", "修改不计免赔"),
	MODIFY_RENTTIME("2", "修改取车时间"),
	MODIFY_REVERTTIME("3", "修改还车时间"),
	MODIFY_GETADDR("4", "修改取车地址"),
	MODIFY_RETURNADDR("5", "修改还车地址"),
	MODIFY_DRIVER("6", "修改附加驾驶人"),
	MODIFY_SRVGETFLAG("7", "修改取车标志"),
	MODIFY_SRVRETURNFLAG("8", "修改还车标志"),
	MODIFY_USERCOINFLAG("9", "修改使用凹凸币"),
	MODIFY_CAROWNERCOUPON("10", "修改车主券"),
	MODIFY_GETRETURNCOUPON("11", "修改取还车券"),
	MODIFY_PLATFORMCOUPON("12", "修改平台券")
    ;


    private String code;

    private String name;

    OrderChangeItemEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
