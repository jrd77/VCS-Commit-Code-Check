package com.atzuche.config.common.entity;

import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/27 4:27 下午
 **/
public class IllegalDepositConfigEntity implements Serializable {
    private Integer id;
    private Integer cityCode;
    private Integer leaseMin;
    private Integer leaseMax;
    private Integer depositAmt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCityCode() {
        return cityCode;
    }

    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    public Integer getLeaseMin() {
        return leaseMin;
    }

    public void setLeaseMin(Integer leaseMin) {
        this.leaseMin = leaseMin;
    }

    public Integer getLeaseMax() {
        return leaseMax;
    }

    public void setLeaseMax(Integer leaseMax) {
        this.leaseMax = leaseMax;
    }

    public Integer getDepositAmt() {
        return depositAmt;
    }

    public void setDepositAmt(Integer depositAmt) {
        this.depositAmt = depositAmt;
    }
}
