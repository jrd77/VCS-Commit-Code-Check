package com.atzuche.config.common.api;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/27 2:53 下午
 **/
public class ConfigNotFoundException extends RuntimeException {
    private String errorCode ="400036";

    private String errorMsg = "系统基础参数配置异常";


    public ConfigNotFoundException() {
        super();
    }
    public ConfigNotFoundException(String message){
        super(message);
    }

    public String getErrorCode() {
        return errorCode;
    }



    public String getErrorMsg() {
        return errorMsg;
    }

}
