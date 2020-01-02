package com.atzuche.config.common.api;

/**
 * 配置的类型，
 * 目前暂时支持单整数型、单字符串类型、表类型（list类型）
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/26 3:58 下午
 **/
public enum ConfigType {
    SINGLE_INT(1,"int"),SINGLE_STRING(2,"string"),TABLE(3,"table");
    private int type;
    private String typeName;

    ConfigType(int type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }
}
