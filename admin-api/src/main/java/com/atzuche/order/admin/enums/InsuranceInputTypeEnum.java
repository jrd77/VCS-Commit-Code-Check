package com.atzuche.order.admin.enums;


public enum InsuranceInputTypeEnum {
    MANUAL("1", "手工录入"),
    IMPORT("2", "批量导入"),
    BI_IMPORT("3", "BI导入");


    private String type;

    private String typeDescription;

    InsuranceInputTypeEnum(String type, String typeDescription) {
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
        for (InsuranceInputTypeEnum remarkType : InsuranceInputTypeEnum.values()) {
            if(remarkType.getType().equals(type)){
                return remarkType.getTypeDescription();
            }
        }
        return "";
    }
}
