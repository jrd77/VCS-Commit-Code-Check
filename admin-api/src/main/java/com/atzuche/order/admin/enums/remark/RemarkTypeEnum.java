package com.atzuche.order.admin.enums.remark;


public enum RemarkTypeEnum {
    CLAIMS("1", "理赔备注"),
    TIME_DELAY("2", "限制租客延时备注"),
    VIOLATION("3", "违章备注"),
    VIOLATION_HANDLER("4", "违章处理备注"),
    TRANSACTION("5", "交易备注"),
    CALL_CENTER("6", "呼叫中心备注"),
    FOLLOW_UP("7", "跟进备注"),
    LIQUIDATION("8", "运营清算备注"),
    TELEMARKETING_CENTER("9", "电销中心备注"),
    OPERATION("10", "商品运营备注"),
    RISK("11", "风控备注"),
    CAR_SERVICE("12", "取送车备注");


    private String type;

    private String typeDescription;

    RemarkTypeEnum(String type, String typeDescription) {
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
        for (RemarkTypeEnum remarkType : RemarkTypeEnum.values()) {
            if(remarkType.getType().equals(type)){
                return remarkType.getTypeDescription();
            }
        }
        return "";
    }
}
