/**
 * 
 */
package com.atzuche.order.commons.enums.sys;

import lombok.Getter;

/**
 * @author jing.huang
 *
 */
@Getter
public enum Env {
	pro("1","正式环境"),
    test("2","测试环境");

    private String code;

    private String name;

    Env(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(String code){
        if(code == null){
            return null;
        }
        Env[] values = Env.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getCode().equals(code)){
                return values[i].name;
            }
        }
        return null;
    }
}
