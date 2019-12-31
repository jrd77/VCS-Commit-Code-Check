package com.atzuche.config.common.entity;

import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/27 4:27 下午
 **/
public class CarGpsRuleEntity implements Serializable {
    private Integer id;
    private Integer startDate;
    private Integer serialNumber;
    private String days;
    private String fees;
    private Integer ruleStatus;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStartDate() {
        return startDate;
    }

    public void setStartDate(Integer startDate) {
        this.startDate = startDate;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public Integer getRuleStatus() {
        return ruleStatus;
    }

    public void setRuleStatus(Integer ruleStatus) {
        this.ruleStatus = ruleStatus;
    }

    @Override
    public String toString() {
        return "CarGpsRuleEntity{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", serialNumber=" + serialNumber +
                ", days='" + days + '\'' +
                ", fees='" + fees + '\'' +
                ", ruleStatus=" + ruleStatus +
                '}';
    }
}
