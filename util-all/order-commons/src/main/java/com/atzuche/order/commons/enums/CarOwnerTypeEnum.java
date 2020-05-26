package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum CarOwnerTypeEnum {
    GRCZ(5,"个人车主",1),
    CZTGC(20,"长租托管车",1),
    DZTGC(30,"短租托管车",2),
    DGCL(35,"代管车辆",3),
    DGCZGDBC(150,"代管车专供代步车",3),
    ATHZFGS(65,"凹凸合资分公司",2),
    TKFSCL(80,"坦客分时车辆",1),
    YJDKHCZ(90,"一级大客户车主",2),
    EJDKHCZ(95,"二级大客户车主",2),
    SJDKHCZ(100,"三级大客户车主",2),
    ZYACSGYSCL(105,"直营A城市供应商车辆",2),
    GYSKGDBCCL(125,"供应商可供代步车车辆",2),
    GYSBGDBCCL(130,"供应商不供代步车车辆",2),
    DYCXZGDBC(135,"单一车行专供代步车",2),
    QSCXZGDBC(140,"全市车行专供代步车",2),
    QSCXZGDBCYJ(155,"全市车行专供代步车月结",2),
    DYCXZGDBCYJ(160,"单一车行专供代步车月结",2),
    FDBCZZTGC(165,"非代步车长租托管车",2),
    GRCZTGC(170,"个人长租托管车",2),
    QYCZTGC(175,"企业长租托管车",2),
    DBCTGC(180,"代步车托管车",2);

    private int code;
    private String name;
    private int serviceRateType;//1 平台服务费比例，2 固定平台服务费比例，3代官车服务费比例

    CarOwnerTypeEnum(int code, String name, int serviceRateType) {
        this.code = code;
        this.name = name;
        this.serviceRateType = serviceRateType;
    }

    public static String getNameByCode(Integer code){
        if(code == null ){
            return null;
        }
        CarOwnerTypeEnum[] values = CarOwnerTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getCode() == code){
                return values[i].getName();
            }
        }
        return null;
    }
    public static CarOwnerTypeEnum getEnumByCode(Integer code){
        if(code == null ){
            return null;
        }
        CarOwnerTypeEnum[] values = CarOwnerTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getCode() == code){
                return values[i];
            }
        }
        return null;
    }


    /**
     *是否是凹凸自營
     */
    public static Boolean isAuToByCode(Integer code){
        boolean result = false;
        if (code == null) {
            return result;
        }
        if (code == DZTGC.code || code == DGCL.code) {
            result = true;
            return result;
        }
        return result;
    }


}
