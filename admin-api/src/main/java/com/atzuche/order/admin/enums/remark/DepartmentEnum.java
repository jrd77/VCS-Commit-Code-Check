package com.atzuche.order.admin.enums.remark;


public enum DepartmentEnum {
    BXLC("bxlp", "保险理赔"),
    CHSC("chsc", "车后市场"),
    CLSPYY("clspyy", "车辆商品运营"),
    DBCZCZX("dbczczx", "代步车支持中心"),
    DGB("dgb", "代管部"),
    DJBC("djbc", "带驾包车"),
    DXZX("dxzx", "电销中心"),
    EXCSGLB("excsglb", "二线城市管理部"),
    FKZX("fkzx", "风控中心"),
    HJZX("hjzx", "呼叫中心"),
    JYZCZX("jyzczx", "交易支持中心"),
    QGCLFWZX("qgclfwzx", "全国车辆服务中心"),
    XTYYZX("xtyyzx", "系统应用中心"),
    YWZCB("ywzcb", "业务支持部"),
    ZJB("zjb", "质检部");


    private String type;

    private String typeDescription;

    DepartmentEnum(String type, String typeDescription) {
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
        for (DepartmentEnum remarkType : DepartmentEnum.values()) {
            if(remarkType.getType().equals(type)){
                return remarkType.getTypeDescription();
            }
        }
        return "";
    }
}
