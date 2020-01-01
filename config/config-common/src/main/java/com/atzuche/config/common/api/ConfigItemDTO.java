package com.atzuche.config.common.api;

import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/26 3:57 下午
 **/
public class ConfigItemDTO implements Serializable {
    private String configName;
    private String configValue;
    private String configType;
    private Class configClass;

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public Class getConfigClass() {
        return configClass;
    }

    public void setConfigClass(Class configClass) {
        this.configClass = configClass;
    }
}
