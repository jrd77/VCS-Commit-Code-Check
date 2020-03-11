package com.atzuche.order.cashieraccount.common;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/10 11:43 上午
 **/
public enum VirtualPayTypeEnum {
    PAY("01"),REFUND("04");
    private String value;

    VirtualPayTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
