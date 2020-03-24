package com.atzuche.config.common.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * 
 * @author zhangbin
 * @date 2020-02-21 11:36:28
 * @Description:
 */
public class ServicePointEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//地址标题
	private String addressTitle;
	//详细地址
	private String addressContent;
	//纬度
	private Double lat;
	//经度
	private Double lon;
	//城市编码
	private Integer cityCode;
	//城市名称
	private String cityName;
	//是否是精确的地址:0否，1是
	private Integer isExact;
	//是否机场:0否，1是
	private Integer isAirport;
	//是否支持取送车:0否，1是
	private Integer getReturnFlag;
	//是否失效:0否，1是
	private Integer delFlag;
	//
	private Date createTime;
	//
	private String createOperator;
	//
	private Date updateTime;
	//
	private String updateOperator;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddressTitle() {
        return addressTitle;
    }

    public void setAddressTitle(String addressTitle) {
        this.addressTitle = addressTitle;
    }

    public String getAddressContent() {
        return addressContent;
    }

    public void setAddressContent(String addressContent) {
        this.addressContent = addressContent;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Integer getCityCode() {
        return cityCode;
    }

    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getIsExact() {
        return isExact;
    }

    public void setIsExact(Integer isExact) {
        this.isExact = isExact;
    }

    public Integer getIsAirport() {
        return isAirport;
    }

    public void setIsAirport(Integer isAirport) {
        this.isAirport = isAirport;
    }

    public Integer getGetReturnFlag() {
        return getReturnFlag;
    }

    public void setGetReturnFlag(Integer getReturnFlag) {
        this.getReturnFlag = getReturnFlag;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateOperator() {
        return createOperator;
    }

    public void setCreateOperator(String createOperator) {
        this.createOperator = createOperator;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateOperator() {
        return updateOperator;
    }

    public void setUpdateOperator(String updateOperator) {
        this.updateOperator = updateOperator;
    }
}
