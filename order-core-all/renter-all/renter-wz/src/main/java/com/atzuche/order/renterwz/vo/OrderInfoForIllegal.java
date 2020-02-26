package com.atzuche.order.renterwz.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value= Include.NON_EMPTY)
public class OrderInfoForIllegal {
    /**
     * 订单号
      */
	private String orderno;
	/**
	 * 取车时间
	 */
	private String qtime;
	/**
	 * 还车时间
	 */
	private String htime;
	/**
	 * 实际还车时间
	 */
	private String acthtime;
	/**
	 * 租客手机号
	 */
	private String zkphone;
	/**
	 * 车主手机号
	 */
	private String czphone;
	/**
	 * 车辆类型
	 */
	private String cartype;
	/**
	 * 车牌号
	 */
	private String platenum;
	/**
	 * 车架号
	 */
	private String frameno;
	/**
	 * 发动机号
	 */
	private String enginenum;
	/**
	 * 用车城市
	 */
	private String yccity;
	/**
	 * 途径城市
	 */
	private String tjcity;
	private Integer ownerType;
	private String offlineOrderType;
	private String entryCode;
	private Integer ownerOfflineOrderStatus;
	/**
	 * 车辆注册号
	 */
	private String carNo;
	/**
	 * 行驶证到期日期
	 */
	private Date licenseExpire;
	/**
	 * 成功订单数
	 */
	private Integer successCount;
	/**
	 * 租客数
	 */
	private Integer renterCount;
	/**
	 * 违章数
	 */
	private Integer illegalCount;
	/**车主会员号*/
	private String ownerNo;
	/**租客会员号*/
	private String renterNo;
    /** 订单类型*/
	private String orderType;
	/**渠道*/
	private String channelType;
	/**动力源*/
	private String engineSource;

	private String moreLicenseFlag;

	public String getMoreLicenseFlag() {
		return moreLicenseFlag;
	}

	public void setMoreLicenseFlag(String moreLicenseFlag) {
		this.moreLicenseFlag = moreLicenseFlag;
	}

	public String getOwnerNo() {
		return ownerNo;
	}

	public void setOwnerNo(String ownerNo) {
		this.ownerNo = ownerNo;
	}

	public String getRenterNo() {
		return renterNo;
	}

	public void setRenterNo(String renterNo) {
		this.renterNo = renterNo;
	}

	public Integer getOwnerType() {
		return ownerType;
	}
	public void setOwnerType(Integer ownerType) {
		this.ownerType = ownerType;
	}
	public String getOrderno() {
		return orderno;
	}
	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}
	public String getQtime() {
		return qtime;
	}
	public void setQtime(String qtime) {
		this.qtime = qtime;
	}
	public String getHtime() {
		return htime;
	}
	public void setHtime(String htime) {
		this.htime = htime;
	}
	public String getActhtime() {
		return acthtime;
	}
	public void setActhtime(String acthtime) {
		this.acthtime = acthtime;
	}
	public String getZkphone() {
		return zkphone;
	}
	public void setZkphone(String zkphone) {
		this.zkphone = zkphone;
	}
	public String getCzphone() {
		return czphone;
	}
	public void setCzphone(String czphone) {
		this.czphone = czphone;
	}
	public String getCartype() {
		return cartype;
	}
	public void setCartype(String cartype) {
		this.cartype = cartype;
	}
	public String getPlatenum() {
		return platenum;
	}
	public void setPlatenum(String platenum) {
		this.platenum = platenum;
	}
	public String getFrameno() {
		return frameno;
	}
	public void setFrameno(String frameno) {
		this.frameno = frameno;
	}
	public String getEnginenum() {
		return enginenum;
	}
	public void setEnginenum(String enginenum) {
		this.enginenum = enginenum;
	}
	public String getYccity() {
		return yccity;
	}
	public void setYccity(String yccity) {
		this.yccity = yccity;
	}
	public String getTjcity() {
		return tjcity;
	}
	public void setTjcity(String tjcity) {
		this.tjcity = tjcity;
	}
    public String getOfflineOrderType() {
        return offlineOrderType;
    }
    public void setOfflineOrderType(String offlineOrderType) {
        this.offlineOrderType = offlineOrderType;
    }
    public String getEntryCode() {
        return entryCode;
    }
    public void setEntryCode(String entryCode) {
        this.entryCode = entryCode;
    }
    public Integer getOwnerOfflineOrderStatus() {
        return ownerOfflineOrderStatus;
    }
    public void setOwnerOfflineOrderStatus(Integer ownerOfflineOrderStatus) {
        this.ownerOfflineOrderStatus = ownerOfflineOrderStatus;
    }

    public String getCarNo() {
		return carNo;
	}
	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}
	
	public Date getLicenseExpire() {
		return licenseExpire;
	}
	public void setLicenseExpire(Date licenseExpire) {
		this.licenseExpire = licenseExpire;
	}
	
	public Integer getSuccessCount() {
		return successCount;
	}
	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}
	public Integer getRenterCount() {
		return renterCount;
	}
	public void setRenterCount(Integer renterCount) {
		this.renterCount = renterCount;
	}
	public Integer getIllegalCount() {
		return illegalCount;
	}
	public void setIllegalCount(Integer illegalCount) {
		this.illegalCount = illegalCount;
	}
	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public String getEngineSource() {
		return engineSource;
	}

	public void setEngineSource(String engineSource) {
		this.engineSource = engineSource;
	}

	@Override
    public String toString() {
        return "OrderInfoForIllegalBo{" +
                "orderno='" + orderno + '\'' +
                ", qtime='" + qtime + '\'' +
                ", htime='" + htime + '\'' +
                ", acthtime='" + acthtime + '\'' +
                ", zkphone='" + zkphone + '\'' +
                ", czphone='" + czphone + '\'' +
                ", cartype='" + cartype + '\'' +
                ", platenum='" + platenum + '\'' +
                ", frameno='" + frameno + '\'' +
                ", enginenum='" + enginenum + '\'' +
                ", yccity='" + yccity + '\'' +
                ", tjcity='" + tjcity + '\'' +
                ", ownerType=" + ownerType +
                ", offlineOrderType=" + offlineOrderType +
                ", entryCode='" + entryCode + '\'' +
                ", ownerOfflineOrderStatus=" + ownerOfflineOrderStatus +
                '}';
    }
}
