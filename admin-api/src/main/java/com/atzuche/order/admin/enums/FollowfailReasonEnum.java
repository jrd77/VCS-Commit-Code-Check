package com.atzuche.order.admin.enums;


public enum FollowfailReasonEnum {
    ZKFQ_ZFSB("1", "租客放弃-支付失败"),
    ZKFQ_JJCD("2", "租客放弃-拒绝促单"),
    ZKFQ_DHBT("3", "租客放弃-电话不通"),
    ZKFQ_BYYZF("4", "租客放弃-不愿意再等待"),
    ZKFQ_ZKXQBG("5", "租客放弃-租客需求变更"),
    ZKFQ_QTYY("6", "租客放弃-其他原因"),
    ZKZJZDC("7", "租客自己找到车"),
    ZBDC_PPCXBF("8", "找不到车-品牌车型不符"),
    ZBDC_JGBF("9", "找不到车-价格不符"),
    ZBDC_QCWZBF("10", "找不到车-取车位置不符"),
    ZBDC_LBJFW("11", "找不到车-来不及服务"),
    ABDC_QTYY("12", "找不到车-其他原因"),
    QTYY_ZKHCBTG("13", "其他原因-租客核查不通过"),
    QTYY_XTWT("14", "其他原因-系统问题"),
    QTYY_NBCS("15", "其他原因-内部测试"),
    QTYY_YWC("16", "其他原因-已完成"),
    QTYY_CXZC("17", "其他原因-重新找车");

    private String type;

    private String typeDescription;

    FollowfailReasonEnum(String type, String typeDescription) {
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
        for (FollowfailReasonEnum remarkType : FollowfailReasonEnum.values()) {
            if(remarkType.getType().equals(type)){
                return remarkType.getTypeDescription();
            }
        }
        return "";
    }
}
