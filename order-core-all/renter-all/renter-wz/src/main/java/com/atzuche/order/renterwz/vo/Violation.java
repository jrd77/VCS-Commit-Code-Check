package com.atzuche.order.renterwz.vo;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

public class Violation implements Serializable{

    private static final long serialVersionUID = 2655863682018642492L;

    /** 违章编码,唯一，非违章条例码 **/
    private String code;
    /** 违章时间 **/
    private Date time;
    /** 违章地址 **/
    private String address;
    /** 违章处理原因 **/
    private String reason;
    /** 罚款金额 **/
    private String fine;
    /** 违章扣分 **/
    private int point;
    /** 违章发生城市，可能为空 **/
    private String violationCity;
    /** 罚款标准省份 **/
    private String province;
    /** 罚款标准城市 **/
    private String city;
    /** 能否勾选办理：0不可勾选, 1可勾选。 **/
    private int canSelect;
    /** 违章处理状态：1：未处理，2：处理中，3：已处理 **/
    private int processStatus;
    /** 违章缴费状态 不返回表示无法获取该信息，1-未缴费 2-已缴费 **/
    private int paymentStatus;
    /**
     * 车乐行返回错误码
     **/
    private String clxCode;
    /**
     * 车乐行返回内容
     **/
    private String clxResult;
    /**
     * 车乐行车辆信息错误，额外返回errors字段
     **/
    private String clxErrors;
    /**
     * 车乐行车辆信息错误，额外返回errors字段文案
     **/
    private String clxErrorsMsg;
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getFine() {
        return fine;
    }

    public void setFine(String fine) {
        this.fine = fine;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getViolationCity() {
        return violationCity;
    }

    public void setViolationCity(String violationCity) {
        this.violationCity = violationCity;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCanSelect() {
        return canSelect;
    }

    public void setCanSelect(int canSelect) {
        this.canSelect = canSelect;
    }

    public int getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(int processStatus) {
        this.processStatus = processStatus;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getClxCode() {
		return clxCode;
	}

	public void setClxCode(String clxCode) {
		this.clxCode = clxCode;
	}

	public String getClxResult() {
		return clxResult;
	}

	public void setClxResult(String clxResult) {
		this.clxResult = clxResult;
	}

	public String getClxErrors() {
		return clxErrors;
	}

	public void setClxErrors(String clxErrors) {
		this.clxErrors = clxErrors;
	}

	public String getClxErrorsMsg() {
		return clxErrorsMsg;
	}

	public void setClxErrorsMsg(String clxErrorsMsg) {
		this.clxErrorsMsg = clxErrorsMsg;
	}

	@Override
    public String toString() {
        return "Violations{" +
                "code='" + code + '\'' +
                ", time=" + time +
                ", address='" + address + '\'' +
                ", reason='" + reason + '\'' +
                ", fine='" + fine + '\'' +
                ", point=" + point +
                ", violationCity='" + violationCity + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", canSelect=" + canSelect +
                ", processStatus=" + processStatus +
                ", paymentStatus=" + paymentStatus +
                '}';
    }

	@Override
    public boolean equals(Object obj) {
        if(this == obj){
            //地址相等
            return true;
        }

        if(obj == null){
            //非空性：对于任意非空引用x，x.equals(null)应该返回false。
            return false;
        }

        if(obj instanceof Violation){
        	Violation other = (Violation) obj;
            //需要比较的字段相等，则这两个对象相等
            if(equalsStr(this.code, other.code) && 
            		equalsStr(this.address, other.address)
                    && equalsDate(this.time, other.time)){
                return true;
            }
        }

        return false;
    }

    private boolean equalsStr(String str1, String str2){
        if(StringUtils.isEmpty(str1) && StringUtils.isEmpty(str2)){
            return true;
        }
        if(!StringUtils.isEmpty(str1) && str1.equals(str2)){
            return true;
        }
        return false;
    }
    
    private boolean equalsDate(Date str1, Date str2){
        if(str1 == null && str2 == null){
            return true;
        }
        if(str1 != null && str2 != null && str1.getTime() == str2.getTime()){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (code == null ? 0 : code.hashCode());
        result = 31 * result + (address == null ? 0 : address.hashCode());
        result = 31 * result + (time == null ? 0 : time.hashCode());
        return result;
    }
}
