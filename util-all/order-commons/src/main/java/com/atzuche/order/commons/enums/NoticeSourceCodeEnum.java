package com.atzuche.order.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeSourceCodeEnum {
    renter(1,"租客"),
    owner(2,"车主");
    private int code;
    private String desc;


    public static NoticeSourceCodeEnum getEnumByCode(int code){
        NoticeSourceCodeEnum[] values = NoticeSourceCodeEnum.values();
        for(int i=0;i<values.length;i++){
            if(values[i].getCode() == code){
                return values[i];
            }
        }
        return null;
    }
}
