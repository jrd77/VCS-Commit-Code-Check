package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.ToString;

import java.util.Date;

/**
 * @author ：weixu.chen
 * @date ：Created in 2019/12/17 20:45
 */
@ToString
public class CommUseDriverInfoDTO {

    @AutoDocProperty(value = "主键id")
    private Integer id;

    @AutoDocProperty(value = "真实姓名")
    private String realName;

    @AutoDocProperty(value = "手机号")
    private String mobile;

    @AutoDocProperty(value = "身份证")
    private String idCard;

    @AutoDocProperty(value = "准驾车型")
    private String driLicAllowCar;

    @AutoDocProperty(value = "驾驶证有效起始日期")
    private Date validityStartDate;

    @AutoDocProperty(value = "驾驶证有效终止日期")
    private Date validityEndDate;

    @AutoDocProperty(value = "是否认证 0-未认证，1-认证通过，2认证失败")
    private String isAuth;
    
    ///附加驾驶人金额
    private String amt;
    //管理后台操作人名称
    private String consoleOperatorName;
    
    
    public String getConsoleOperatorName() {
		return consoleOperatorName;
	}

	public void setConsoleOperatorName(String consoleOperatorName) {
		this.consoleOperatorName = consoleOperatorName;
	}

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getDriLicAllowCar() {
        return driLicAllowCar;
    }

    public void setDriLicAllowCar(String driLicAllowCar) {
        this.driLicAllowCar = driLicAllowCar;
    }

    public Date getValidityStartDate() {
        return validityStartDate;
    }

    public void setValidityStartDate(Date validityStartDate) {
        this.validityStartDate = validityStartDate;
    }

    public Date getValidityEndDate() {
        return validityEndDate;
    }

    public void setValidityEndDate(Date validityEndDate) {
        this.validityEndDate = validityEndDate;
    }

    public String getIsAuth() {
        return isAuth;
    }

    public void setIsAuth(String isAuth) {
        this.isAuth = isAuth;
    }

    @Override
    public String toString() {
        return "CommUseDriverInfo{" +
                "id=" + id +
                ", realName='" + realName + '\'' +
                ", mobile=" + mobile +
                ", idCard='" + idCard + '\'' +
                ", driLicAllowCar='" + driLicAllowCar + '\'' +
                ", validityStartDate=" + validityStartDate +
                ", validityEndDate=" + validityEndDate +
                ", isAuth='" + isAuth + '\'' +
                '}';
    }
}
