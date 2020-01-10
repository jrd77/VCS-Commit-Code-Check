package com.atzuche.order.admin.enums;


public enum FollowStatusEnum {
    WGJ("1", "未跟进"),
    GJCG("2", "跟进成功"),
    GJSB("3", "跟进失败"),
    ZCZ("4", "找车中"),
    CFXQ("5", "重复需求"),
    SCBJ("6", "首次被拒"),
    DXCX("7", "短信促单");


    private String type;

    private String typeDescription;

    FollowStatusEnum(String type, String typeDescription) {
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
        for (FollowStatusEnum remarkType : FollowStatusEnum.values()) {
            if(remarkType.getType().equals(type)){
                return remarkType.getTypeDescription();
            }
        }
        return "";
    }
}
