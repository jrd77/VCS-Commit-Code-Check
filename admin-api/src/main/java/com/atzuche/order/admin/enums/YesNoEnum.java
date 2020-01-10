package com.atzuche.order.admin.enums;


public enum YesNoEnum {
    NO("0", "是"),
    YES("1", "否");


    private String type;

    private String typeDescription;

    YesNoEnum(String type, String typeDescription) {
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
        for (YesNoEnum remarkType : YesNoEnum.values()) {
            if(remarkType.getType().equals(type)){
                return remarkType.getTypeDescription();
            }
        }
        return "";
    }
}
