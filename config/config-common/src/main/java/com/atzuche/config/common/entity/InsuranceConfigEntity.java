package com.atzuche.config.common.entity;

import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/27 4:25 下午
 **/
public class InsuranceConfigEntity implements Serializable {
    private Integer id;
    private Integer guidPriceBegin;
    private Integer guidPriceEnd;
    private Integer insuranceValue;
    private Integer superSupportRisk;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGuidPriceBegin() {
        return guidPriceBegin;
    }

    public void setGuidPriceBegin(Integer guidPriceBegin) {
        this.guidPriceBegin = guidPriceBegin;
    }

    public Integer getGuidPriceEnd() {
        return guidPriceEnd;
    }

    public void setGuidPriceEnd(Integer guidPriceEnd) {
        this.guidPriceEnd = guidPriceEnd;
    }

    public Integer getInsuranceValue() {
        return insuranceValue;
    }

    public void setInsuranceValue(Integer insuranceValue) {
        this.insuranceValue = insuranceValue;
    }

    public Integer getSuperSupportRisk() {
        return superSupportRisk;
    }

    public void setSuperSupportRisk(Integer superSupportRisk) {
        this.superSupportRisk = superSupportRisk;
    }

    @Override
    public String toString() {
        return "InsuranceConfigEntity{" +
                "id=" + id +
                ", guidPriceBegin=" + guidPriceBegin +
                ", guidPriceEnd=" + guidPriceEnd +
                ", insuranceValue=" + insuranceValue +
                ", superSupportRisk=" + superSupportRisk +
                '}';
    }
}
