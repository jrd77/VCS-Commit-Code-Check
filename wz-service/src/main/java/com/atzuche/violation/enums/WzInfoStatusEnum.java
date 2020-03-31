package com.atzuche.violation.enums;

/**
 * 违章信息
 * 1未查询、2查询失败、3已查询-无违章、4已查询-有违章、5历史数据
 */
public enum  WzInfoStatusEnum {
    STATUS_1(1,"未查询"),
    STATUS_2(2,"查询失败"),
    STATUS_3(3,"无违章"),
    STATUS_4(4,"有违章"),
    STATUS_5(5,"历史数据"),
;
    private Integer status;

    private String statusDesc;

    WzInfoStatusEnum(Integer status, String statusDesc) {
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
        WzInfoStatusEnum[] values = WzInfoStatusEnum.values();
        for (WzInfoStatusEnum value : values) {
            if(status.equals(value.getStatus())){
                return value.getStatusDesc();
            }
        }
        return "";
    }
}
