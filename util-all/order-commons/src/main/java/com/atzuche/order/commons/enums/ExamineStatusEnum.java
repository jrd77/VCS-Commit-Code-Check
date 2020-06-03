package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum ExamineStatusEnum {

	NOT_APPROVED(0,"未审核"),
	APPROVED(1,"审核通过"),
	AUDIT_FAILED(2,"审核不通过"),
	UNDER_REVIEW_EXCEPTION(3,"审核中，待核查（异常）"),
	UNDER_REVIEW_TEST(4,"审核中，待核查（测试）"),
	OTHER(5,"其他");

    private int code;
    private String name;

    ExamineStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName(Integer code){
         if(code == null){
            return null;
         }
        EffectiveEnum[] values = EffectiveEnum.values();
        for(int i=0;i<values.length;i++){
            if(code == values[i].getCode()){
                return values[i].getName();
            }
        }
        return null;
    }

}
