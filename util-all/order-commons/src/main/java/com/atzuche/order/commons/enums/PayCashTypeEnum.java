package com.atzuche.order.commons.enums;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/10 11:57 上午
 **/
public enum  PayCashTypeEnum {
    RENTER_COST("11","租车费用"),DEPOSIT("01","车辆押金"),WZ_DEPOSIT("02","违章押金");
    private String value;
    private String name;

    PayCashTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }



    public String getName() {
        return name;
    }



    public static PayCashTypeEnum fromValue(String value){
        PayCashTypeEnum[] values = values();
         for(PayCashTypeEnum payCashTypeEnum:values){
             if(payCashTypeEnum.value.equals(value)){
                 return payCashTypeEnum;
             }
         }
         throw new RuntimeException("value:"+value+" is invalid");
    }
}
