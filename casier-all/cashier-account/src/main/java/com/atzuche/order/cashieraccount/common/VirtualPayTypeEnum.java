package com.atzuche.order.cashieraccount.common;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/10 11:43 上午
 **/
public enum VirtualPayTypeEnum {
    PAY(1),REFUND(-1);
    private int value;

    VirtualPayTypeEnum(int value) {
        this.value = value;
    }
}
