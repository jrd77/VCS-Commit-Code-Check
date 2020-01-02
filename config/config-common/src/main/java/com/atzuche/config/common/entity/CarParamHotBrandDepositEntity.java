package com.atzuche.config.common.entity;

import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/30 3:25 下午
 **/
public class CarParamHotBrandDepositEntity implements Serializable {
    private Integer brandId;
    private String brandTxt;
    private Integer typeId;
    private String typeTxt;
    private String configValue;

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getBrandTxt() {
        return brandTxt;
    }

    public void setBrandTxt(String brandTxt) {
        this.brandTxt = brandTxt;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeTxt() {
        return typeTxt;
    }

    public void setTypeTxt(String typeTxt) {
        this.typeTxt = typeTxt;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}
