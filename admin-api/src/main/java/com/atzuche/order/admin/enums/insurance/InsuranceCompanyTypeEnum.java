package com.atzuche.order.admin.enums.insurance;


public enum InsuranceCompanyTypeEnum {
    CPIC("1", "太平洋保险"),
    PICC("2", "中国人民保险");


    private String type;

    private String typeDescription;

    InsuranceCompanyTypeEnum(String type, String typeDescription) {
        this.type = type;
        this.typeDescription = typeDescription;
    }

    public String getType() {
        return type;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    /**
     * 获取枚举描述信息
     * @param type
     * @return
     */
    public static String getDescriptionByType(String type){
        for (InsuranceCompanyTypeEnum remarkType : InsuranceCompanyTypeEnum.values()) {
            if(remarkType.getType().equals(type)){
                return remarkType.getTypeDescription();
            }
        }
        return "";
    }
}
