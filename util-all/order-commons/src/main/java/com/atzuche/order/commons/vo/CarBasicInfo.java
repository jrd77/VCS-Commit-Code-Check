package com.atzuche.order.commons.vo;

import com.autoyol.doc.annotation.AutoDocProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;

@JsonInclude(value=Include.NON_EMPTY)
public class CarBasicInfo implements Serializable{

    private static final long serialVersionUID = 8643731760255625630L;

    @AutoDocProperty(value = "图片路径,作为封面的图片地址,相对路径")
    private String picPath;

    @AutoDocProperty(value = "车牌号")
    private String plateNum;

    @AutoDocProperty(value = "品牌车型信息,如:奥迪")
    private String brandInfo;

    @AutoDocProperty(value = "排量,如:3.0T")
    private String sweptVolum;

    @AutoDocProperty(value = "手动、自动类型,手动1，自动2")
    private String gbType;

    @AutoDocProperty(value = "日限里程,如:日均限200km、日均限300km、不限里程 ")
    private String limitMileage;

    @AutoDocProperty(value = "车辆注册号")
    private String carNo;
    
    @AutoDocProperty(value = "【5.6新增】车辆状态")
    private String carStatus;

    @AutoDocProperty(value = "【5.6新增】前端显示话术")
    private String toast;

    public String getPlateNum() {
        return plateNum;
    }
    public void setPlateNum(String plateNum) {
        this.plateNum = plateNum;
    }
    public String getBrandInfo() {
        return brandInfo;
    }
    public void setBrandInfo(String brandInfo) {
        this.brandInfo = brandInfo;
    }
    public String getPicPath() {
        return picPath;
    }
    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }
    public String getSweptVolum() {
        return sweptVolum;
    }
    public void setSweptVolum(String sweptVolum) {
        this.sweptVolum = sweptVolum;
    }
    public String getGbType() {
        return gbType;
    }
    public void setGbType(String gbType) {
        this.gbType = gbType;
    }
    public String getLimitMileage() {
        return limitMileage;
    }
    public void setLimitMileage(String limitMileage) {
        this.limitMileage = limitMileage;
    }
    public String getCarNo() {
        return carNo;
    }
    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    @Override
    public String toString() {
        return "CarBasicInfo{" +
                "picPath='" + picPath + '\'' +
                ", plateNum='" + plateNum + '\'' +
                ", brandInfo='" + brandInfo + '\'' +
                ", sweptVolum='" + sweptVolum + '\'' +
                ", gbType='" + gbType + '\'' +
                ", limitMileage='" + limitMileage + '\'' +
                ", carNo='" + carNo + '\'' +
                '}';
    }
	public String getCarStatus() {
		return carStatus;
	}
	public void setCarStatus(String carStatus) {
		this.carStatus = carStatus;
	}
	public String getToast() {
		return toast;
	}
	public void setToast(String toast) {
		this.toast = toast;
	}
}
