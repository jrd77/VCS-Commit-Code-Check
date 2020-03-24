package com.atzuche.violation.enums;


public enum WzManageMentEnums {
    RENTER(1,"租客办理"),
    PLATFORM(2,"凹凸办理"),
    OWNER(3,"车主办理"),
    NONE(4,"无数据")
    ;

    private Integer status;

    private String statusDesc;

    WzManageMentEnums(Integer status, String statusDesc) {
        this.status = status;
        this.statusDesc = statusDesc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public static String getStatusDesc(Integer status){
        if(status == null){
            return "";
        }
        WzManageMentEnums[] values = WzManageMentEnums.values();
        for (WzManageMentEnums value : values) {
            if(status.equals(value.getStatus())){
                return value.getStatusDesc();
            }
        }
        return "";
    }
}
