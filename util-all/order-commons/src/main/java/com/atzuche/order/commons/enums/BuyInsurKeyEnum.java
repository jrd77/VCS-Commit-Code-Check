package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum BuyInsurKeyEnum {
	ABATEMENTFLAG("abatementFlag","购买补充全险"),
	TYREINSURFLAG("tyreInsurFlag","购买轮胎保障服务"),
	DRIVERINSURFLAG("driverInsurFlag","购买驾乘无忧保障服务");

    private String key;

    private String name;

    BuyInsurKeyEnum(String key, String name) {
        this.key = key;
        this.name = name;
    }
}
