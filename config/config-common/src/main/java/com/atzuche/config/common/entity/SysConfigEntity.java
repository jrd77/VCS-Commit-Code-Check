package com.atzuche.config.common.entity;

import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/30 3:53 下午
 **/
public class SysConfigEntity implements Serializable {
    private String appType;
    private String itemKey;
    private String itemName;
    private String itemValue;
    private String valueType;


    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    @Override
    public String toString() {
        return "SysConfigEntity{" +
                "appType='" + appType + '\'' +
                ", itemKey='" + itemKey + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemValue='" + itemValue + '\'' +
                ", valueType='" + valueType + '\'' +
                '}';
    }
}
