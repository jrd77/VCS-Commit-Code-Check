package com.atzuche.order.admin.enums;


public enum OperateTypeEnum {
    ADD("1", "新增"),
    UPDATE("2", "更新"),
    DELETE("3", "删除");


    private String type;

    private String typeDescription;

    OperateTypeEnum(String type, String typeDescription) {
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
        for (OperateTypeEnum remarkType : OperateTypeEnum.values()) {
            if(remarkType.getType().equals(type)){
                return remarkType.getTypeDescription();
            }
        }
        return "";
    }
}
