package com.atzuche.order.photo.enums;


public enum UserTypeEnum {
    RENTER("1", "租客上传"),
    OWNER("2", "车主上传"),
    PLATFORM("3", "平台上传");


    private String type;

    private String typeDescription;

    UserTypeEnum(String type, String typeDescription) {
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
        for (UserTypeEnum remarkType : UserTypeEnum.values()) {
            if(remarkType.getType().equals(type)){
                return remarkType.getTypeDescription();
            }
        }
        return "";
    }
}
