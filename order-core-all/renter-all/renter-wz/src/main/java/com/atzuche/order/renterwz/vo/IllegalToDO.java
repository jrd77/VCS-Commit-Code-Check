package com.atzuche.order.renterwz.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * IllegalToDO
 *
 * @author shisong
 * @date 2020/1/2
 */
public class IllegalToDO implements Serializable {

    /** 主键  **/
    private Integer id;

    /** 订单编号 **/
    private String orderNo;

    /** 会员编号 **/
    private String regNo;

    /** 车牌号 **/
    private String plateNum;

    /** 租客电话 **/
    private String renterPhone;

    /** 租客会员号 **/
    private String rentNo;

    /** 车架号 **/
    private String frameNo;

    /** 发动机编号 **/
    private String engineNum;

    /** 起租时间 **/
    private Date rentTime;

    /** 归还时间 **/
    private Date revertTime;

    /** 是否查询：0否 1是  **/
    private Boolean status;

    /** 途径城市 **/
    private String cityName;

    /** 途径城市 **/
    private String cities;

    /** 动力源：1-纯电动，2-汽油，3-油电混动，4-柴油，5-天然气，6-石油气 **/
    private String engineSource;

    /**
     * 是否需要发短信
     */
    private Boolean sendSms;

    /**
     * 类型
     */
    private String type;

    /**
     * 调用车乐行 返回的数据
     */
    private String resStr;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlateNum() {
        return plateNum;
    }

    public void setPlateNum(String plateNum) {
        this.plateNum = plateNum;
    }

    public String getRenterPhone() {
        return renterPhone;
    }

    public void setRenterPhone(String renterPhone) {
        this.renterPhone = renterPhone;
    }

    public String getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(String frameNo) {
        this.frameNo = frameNo;
    }

    public String getEngineNum() {
        return engineNum;
    }

    public void setEngineNum(String engineNum) {
        this.engineNum = engineNum;
    }

    public Date getRentTime() {
        return rentTime;
    }

    public void setRentTime(Date rentTime) {
        this.rentTime = rentTime;
    }

    public Date getRevertTime() {
        return revertTime;
    }

    public void setRevertTime(Date revertTime) {
        this.revertTime = revertTime;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCities() {
        return cities;
    }

    public void setCities(String cities) {
        this.cities = cities;
    }

    public String getEngineSource() {
        return engineSource;
    }

    public void setEngineSource(String engineSource) {
        this.engineSource = engineSource;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getRentNo() {
        return rentNo;
    }

    public void setRentNo(String rentNo) {
        this.rentNo = rentNo;
    }

    public Boolean getSendSms() {
        return sendSms;
    }

    public void setSendSms(Boolean sendSms) {
        this.sendSms = sendSms;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResStr() {
        return resStr;
    }

    public void setResStr(String resStr) {
        this.resStr = resStr;
    }

    @Override
    public String toString() {
        return "IllegalToDO{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", regNo='" + regNo + '\'' +
                ", plateNum='" + plateNum + '\'' +
                ", renterPhone='" + renterPhone + '\'' +
                ", rentNo='" + rentNo + '\'' +
                ", frameNo='" + frameNo + '\'' +
                ", engineNum='" + engineNum + '\'' +
                ", rentTime=" + rentTime +
                ", revertTime=" + revertTime +
                ", status=" + status +
                ", cityName='" + cityName + '\'' +
                ", cities='" + cities + '\'' +
                ", engineSource='" + engineSource + '\'' +
                ", sendSms=" + sendSms +
                ", type='" + type + '\'' +
                ", resStr='" + resStr + '\'' +
                '}';
    }
}
