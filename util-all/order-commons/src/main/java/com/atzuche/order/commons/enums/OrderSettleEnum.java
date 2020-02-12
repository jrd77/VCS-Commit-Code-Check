package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum OrderSettleEnum {

	DEPOSIT_NOT_SETTLED(0,"租车费用/车辆押金未结算"),
	ILLEGAL_DEPOSIT_NOT_SETTLED(1,"租车费用/车辆押金已结算，违章押金未结算"),
	ILLEGAL_DEPOSIT_SETTLED(2,"违章押金已结算 / 违章押金已暂扣"),
	ORDER_CANCEL(3,"订单已取消");

    private Integer code;
    private String desc;

    OrderSettleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
