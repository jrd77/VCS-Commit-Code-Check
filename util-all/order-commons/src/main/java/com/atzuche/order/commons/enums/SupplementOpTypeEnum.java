package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum SupplementOpTypeEnum {
	
	MODIFY_CREATE(1,"修改订单"),
	STEWARD_CREATE(2,"车管家录入"),
	RENTSETTLE_CREATE(3,"租车押金结算"),
	ILLEGALSETTLE_CREATE(4,"违章押金结算"),
	MANUAL_CREATE(5,"手动添加");

    private Integer code;
    private String desc;

    SupplementOpTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
