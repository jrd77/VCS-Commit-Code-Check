package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum CarOwnerTypeEnum {
    A(5,"个人车主",1),
    //B(10,"租赁公司车辆",),
    //C(15,"企业所有车辆" ,),
    D(20,"长租托管车",1),
    //E(25,"托管车辆",),
    F(30,"短租托管车",2),
    G(35,"代管车辆",3),
    H(150,"代管车专供代步车",3),
    //I(40,"合作租赁公司车辆",),
    //J(50,"二手车自营车辆",),
    //K(55,"二手车寄售车辆",),
    //L(60,"内部员工车辆",),
    M(65,"凹凸合资分公司",2),
    //N(75,"无人代管车",),
    O(80,"坦客分时车辆",1),
    //P(85,"小B车主车辆",),
    Q(90,"一级大客户车主",2),
    R(95,"二级大客户车主",2),
    S(100,"三级大客户车主",2),
    T(105,"直营A城市供应商车辆",2),
    //U(110,"直营B城市供应商车辆",),
    //V(120,"供应商专供代步车车辆",),
    W(125,"供应商可供代步车车辆",2),
    X(130,"供应商不供代步车车辆",2),
    Y(135,"单一车行专供代步车",2),
    Z(140,"全市车行专供代步车",2),
    //AA(145,"合作租赁公司专供代步车",),
    AB(155,"全市车行专供代步车月结",2),
    AC(160,"单一车行专供代步车月结",2),
    AD(165,"非代步车长租托管车",2),
    AE(170,"个人长租托管车",2),
    AF(175,"企业长租托管车",2),
    AG(180,"代步车托管车",2);

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
        if (code == F.code || code == G.code) {
            result = true;
            return result;
        }
        return result;
    }
}
