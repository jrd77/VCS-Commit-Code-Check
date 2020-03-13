package com.atzuche.order.cashieraccount.common;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/10 11:57 上午
 **/
public enum  PayCashTypeEnum {
    RENTER_COST(1,"租车费用"),DEPOSIT(2,"车辆押金"),WZ_DEPOSIT(3,"违章押金");
    private int value;
    private String name;

    PayCashTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }



    public String getName() {
        return name;
    }



    public static PayCashTypeEnum fromValue(int value){
        PayCashTypeEnum[] values = values();
         for(PayCashTypeEnum payCashTypeEnum:values){
             if(payCashTypeEnum.value==value){
                 return payCashTypeEnum;
             }
         }
         throw new RuntimeException("value:"+value+" is invalid");
    }
}
