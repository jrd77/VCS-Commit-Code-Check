package com.atzuche.config.common.entity;

import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/30 3:08 下午
 **/
public class DepositConfigEntity implements Serializable {
    private Integer id;
    private Integer depositType;
    private Integer purchasePriceBegin;
    private Integer purchasePriceEnd;
    private Integer depositValue;
    private Integer cityCode;
    private Integer multiple;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDepositType() {
        return depositType;
    }

    public void setDepositType(Integer depositType) {
        this.depositType = depositType;
    }

    public Integer getPurchasePriceBegin() {
        return purchasePriceBegin;
    }

    public void setPurchasePriceBegin(Integer purchasePriceBegin) {
        this.purchasePriceBegin = purchasePriceBegin;
    }

    public Integer getPurchasePriceEnd() {
        return purchasePriceEnd;
    }

    public void setPurchasePriceEnd(Integer purchasePriceEnd) {
        this.purchasePriceEnd = purchasePriceEnd;
    }

    public Integer getDepositValue() {
        return depositValue;
    }

    public void setDepositValue(Integer depositValue) {
        this.depositValue = depositValue;
    }

    public Integer getCityCode() {
        return cityCode;
    }

    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    public Integer getMultiple() {
        return multiple;
    }

    public void setMultiple(Integer multiple) {
        this.multiple = multiple;
    }
}
