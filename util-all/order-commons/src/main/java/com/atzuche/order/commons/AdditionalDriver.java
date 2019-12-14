package com.atzuche.order.commons;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/14 11:13 上午
 **/
public class AdditionalDriver {
    private String name;
    private String mobile;
    private String idNo;
    private String driveLicence;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getDriveLicence() {
        return driveLicence;
    }

    public void setDriveLicence(String driveLicence) {
        this.driveLicence = driveLicence;
    }

    @Override
    public String toString() {
        return "AdditionalDriver{" +
                "name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", idNo='" + idNo + '\'' +
                ", driveLicence='" + driveLicence + '\'' +
                '}';
    }
}
