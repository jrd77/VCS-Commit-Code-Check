package com.atzuche.order.commons.enums.account;

import lombok.Getter;

@Getter
public enum FreeDepositTypeEnum {

    BIND_CARD_FREE(1,"绑卡减免"),
    SESAME_FREE(2,"芝麻减免"),
    CONSUME(3,"支付押金"),  //消费
    ;

    /**
     * 支付状态code
     */
    private Integer code;

    /**
     * 支付状态描述
     */
    private String text;

    FreeDepositTypeEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    /**
     * 根据免押方式编码获取对应枚举信息
     *
     * @param code 免押方式编码
     * @return FreeDepositTypeEnum
     */
    public static FreeDepositTypeEnum getFreeDepositTypeEnumByCode(Integer code) {

        for (FreeDepositTypeEnum freeDepositTypeEnum : FreeDepositTypeEnum.values()) {
            if(freeDepositTypeEnum.getCode() == code) {
                return freeDepositTypeEnum;
            }
        }
        return FreeDepositTypeEnum.CONSUME;
    }
}
