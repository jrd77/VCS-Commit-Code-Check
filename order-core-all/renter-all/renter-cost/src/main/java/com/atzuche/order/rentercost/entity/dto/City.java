package com.atzuche.order.rentercost.entity.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 
 * @comments
 * @author xuyi
 * @version 创建时间：2014年7月22日
 */
public class City {
    
	private int id;// 主键
	@ApiModelProperty("城市编码")
	private String code;// 编码
	@ApiModelProperty("城市名称")
	private String name;// 名称
	@ApiModelProperty("城市备注")
	private String detail;// 备注
	@ApiModelProperty("城市中心店纬度")
	private String lat;// 纬度
	@ApiModelProperty("城市中心店经度")
	private String lon;// 经度
	@ApiModelProperty("城市电话区号")
	private String telPrefix;// 电话区号
	@ApiModelProperty("车牌号前缀")
	private String platePrefix;// 车牌号前缀
	private String type;// 是否开通门店1开通 2未开通
	@ApiModelProperty("是否支持取送车服务,0:不支持,1:支持")
	private String isSupport;// 是否支持取送车服务,0:不支持,1:支持
	private String isOpen;// 0:不开通,1:开通
	private Date createDate; // 创建时间
	private int calculateFlag;// 取送车费用金额重新计算标识 0否，1重新计算
	private int trusteeshipNoHour;// 城市非托管车取送车服务配置小时数
	private int trusteeshipHour;// 城市托管车取送车服务配置小时数
	private int srvGetCost;// 取送车服务的取车费用
	private int srvReturnCost;// 取送车服务的还车费用
	private String orderTimeSet;// 下单时间限制设置（格式：HH:mm:ss）
	private String rentTimeSet;// 租车时间限制设置（格式：HH:mm:ss）
	
	
	private Integer revertstInterval;// 还车服务的间隔时间
	private Integer revertServiceMax ;// 还车服务间隔时间内最大服务数量）
	private Integer rentstInterval;// 取车服务的间隔时间
	private Integer rentServiceMax;// 取车服务的最大服务数
	private Integer allowTransaction;//是否允许交易
	private Integer beforeTime;//提前多少小时下单
	@ApiModelProperty("是否开放太保代步车服务,0关闭，1开启")
	private Integer isCpicCoupon; //是否开放太保代步车服务,0关闭，1开启
	private Integer workBeginTime; //工作开始时间
	private Integer workEndTime; //工作结束时间
	private Integer cpicBeforeTimeWork;	//区分城市，不同城市提前下单时间要求 —— 下单时间距离取车时间 >= A小时   工作时段
	private Integer cpicBeforeTimeFree;	//区分城市，不同城市提前下单时间要求 —— 下单时间距离取车时间 >= A小时  非工作时段
	@ApiModelProperty("取还车范围")
	private String addressRange;//城市范围
	@ApiModelProperty("免费取还车范围")
	private String freeAddressRange;//免费取还车范围
	private String fullLetter;//城市拼音
    private boolean limit;

	/**
	 * 城市的高峰时段，如：09:00-11:30;14:30-17:00
	 */
	private String peakTimeSlot;

	/**
	 * X牌开头非本地,例沪C
	 */
	private String peakStartsWith;

	/**
	 * 非X牌开头非本地,例沪
	 */
	private String peakNotStartsWith;
	
	//提前下单时间，单位小时
	private Integer beforeTransTimeSpan;

	//是否热门城市
	private Integer isHotCity;
	/**无法提供服务的时间起始时间，只显示时间，不显示天,24小时,例如220000*/
	private  Integer unServiceBeginTime;
	/**无法提供服务的时间结束时间,80000*/
	private Integer unServiceEndTime;

	public Integer getUnServiceBeginTime() {
		return unServiceBeginTime;
	}

	public void setUnServiceBeginTime(Integer unServiceBeginTime) {
		this.unServiceBeginTime = unServiceBeginTime;
	}

	public Integer getUnServiceEndTime() {
		return unServiceEndTime;
	}

	public void setUnServiceEndTime(Integer unServiceEndTime) {
		this.unServiceEndTime = unServiceEndTime;
	}

	public Integer getIsHotCity() {
		return isHotCity;
	}

	public void setIsHotCity(Integer isHotCity) {
		this.isHotCity = isHotCity;
	}

	public String getFreeAddressRange() {
		return freeAddressRange;
	}

	public void setFreeAddressRange(String freeAddressRange) {
		this.freeAddressRange = freeAddressRange;
	}

	public Integer getBeforeTime() {
		return beforeTime;
	}

	public void setBeforeTime(Integer beforeTime) {
		this.beforeTime = beforeTime;
	}
	
	public Integer getCpicBeforeTimeWork() {
		return cpicBeforeTimeWork;
	}

	public void setCpicBeforeTimeWork(Integer cpicBeforeTimeWork) {
		this.cpicBeforeTimeWork = cpicBeforeTimeWork;
	}

	public Integer getCpicBeforeTimeFree() {
		return cpicBeforeTimeFree;
	}

	public void setCpicBeforeTimeFree(Integer cpicBeforeTimeFree) {
		this.cpicBeforeTimeFree = cpicBeforeTimeFree;
	}

	public Integer getWorkBeginTime() {
		return workBeginTime;
	}

	public void setWorkBeginTime(Integer workBeginTime) {
		this.workBeginTime = workBeginTime;
	}

	public Integer getWorkEndTime() {
		return workEndTime;
	}

	public void setWorkEndTime(Integer workEndTime) {
		this.workEndTime = workEndTime;
	}

	public Integer getIsCpicCoupon() {
		return isCpicCoupon;
	}

	public void setIsCpicCoupon(Integer isCpicCoupon) {
		this.isCpicCoupon = isCpicCoupon;
	}

	public Integer getRevertstInterval() {
		return revertstInterval;
	}

	public void setRevertstInterval(Integer revertstInterval) {
		this.revertstInterval = revertstInterval;
	}

	public Integer getRevertServiceMax() {
		return revertServiceMax;
	}

	public void setRevertServiceMax(Integer revertServiceMax) {
		this.revertServiceMax = revertServiceMax;
	}

	public Integer getRentstInterval() {
		return rentstInterval;
	}

	public void setRentstInterval(Integer rentstInterval) {
		this.rentstInterval = rentstInterval;
	}

	public Integer getRentServiceMax() {
		return rentServiceMax;
	}

	public void setRentServiceMax(Integer rentServiceMax) {
		this.rentServiceMax = rentServiceMax;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getTelPrefix() {
		return telPrefix;
	}

	public void setTelPrefix(String telPrefix) {
		this.telPrefix = telPrefix;
	}

	public String getPlatePrefix() {
		return platePrefix;
	}

	public void setPlatePrefix(String platePrefix) {
		this.platePrefix = platePrefix;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIsSupport() {
		return isSupport;
	}

	public void setIsSupport(String isSupport) {
		this.isSupport = isSupport;
	}

	public String getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(String isOpen) {
		this.isOpen = isOpen;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getCalculateFlag() {
		return calculateFlag;
	}

	public void setCalculateFlag(int calculateFlag) {
		this.calculateFlag = calculateFlag;
	}

	public int getTrusteeshipNoHour() {
		return trusteeshipNoHour;
	}

	public void setTrusteeshipNoHour(int trusteeshipNoHour) {
		this.trusteeshipNoHour = trusteeshipNoHour;
	}

	public int getTrusteeshipHour() {
		return trusteeshipHour;
	}

	public void setTrusteeshipHour(int trusteeshipHour) {
		this.trusteeshipHour = trusteeshipHour;
	}

	public int getSrvGetCost() {
		return srvGetCost;
	}

	public void setSrvGetCost(int srvGetCost) {
		this.srvGetCost = srvGetCost;
	}

	public int getSrvReturnCost() {
		return srvReturnCost;
	}

	public void setSrvReturnCost(int srvReturnCost) {
		this.srvReturnCost = srvReturnCost;
	}

	public String getOrderTimeSet() {
		return orderTimeSet;
	}

	public void setOrderTimeSet(String orderTimeSet) {
		this.orderTimeSet = orderTimeSet;
	}

	public String getRentTimeSet() {
		return rentTimeSet;
	}

	public void setRentTimeSet(String rentTimeSet) {
		this.rentTimeSet = rentTimeSet;
	}

	@Override
	public String toString() {
		return "City [id=" + id + ", code=" + code + ", name=" + name
				+ ", detail=" + detail + ", lat=" + lat + ", lon=" + lon
				+ ", telPrefix=" + telPrefix + ", platePrefix=" + platePrefix
				+ ", type=" + type + ", isSupport=" + isSupport + ", isOpen="
				+ isOpen + ", createDate=" + createDate + ", calculateFlag="
				+ calculateFlag + ", trusteeshipNoHour=" + trusteeshipNoHour
				+ ", trusteeshipHour=" + trusteeshipHour + ", srvGetCost="
				+ srvGetCost + ", srvReturnCost=" + srvReturnCost
				+ ", orderTimeSet=" + orderTimeSet + ", rentTimeSet="
				+ rentTimeSet + ", revertstInterval=" + revertstInterval
				+ ", revertServiceMax=" + revertServiceMax
				+ ", rentstInterval=" + rentstInterval + ", rentServiceMax="
				+ rentServiceMax + "]";
	}

	public Integer getAllowTransaction() {
		return allowTransaction;
	}

	public void setAllowTransaction(Integer allowTransaction) {
		this.allowTransaction = allowTransaction;
	}

	public String getAddressRange() {
		return addressRange;
	}

	public void setAddressRange(String addressRange) {
		this.addressRange = addressRange;
	}

	public String getFullLetter() {
		return fullLetter;
	}

	public void setFullLetter(String fullLetter) {
		this.fullLetter = fullLetter;
	}

	public String getPeakTimeSlot() {
		return peakTimeSlot;
	}

	public void setPeakTimeSlot(String peakTimeSlot) {
		this.peakTimeSlot = peakTimeSlot;
	}

	public String getPeakStartsWith() {
		return peakStartsWith;
	}

	public void setPeakStartsWith(String peakStartsWith) {
		this.peakStartsWith = peakStartsWith;
	}

	public String getPeakNotStartsWith() {
		return peakNotStartsWith;
	}

	public void setPeakNotStartsWith(String peakNotStartsWith) {
		this.peakNotStartsWith = peakNotStartsWith;
	}

    public boolean isLimit() {
        return limit;
    }

    public void setLimit(boolean limit) {
        this.limit = limit;
    }

	public Integer getBeforeTransTimeSpan() {
		return beforeTransTimeSpan;
	}

	public void setBeforeTransTimeSpan(Integer beforeTransTimeSpan) {
		this.beforeTransTimeSpan = beforeTransTimeSpan;
	}
}
