package com.atzuche.order.vo.request;

import com.autoyol.doc.annotation.AutoDocProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.MDC;

import java.io.Serializable;
import java.util.UUID;

/**
 * api 公共参数
 *
 * @author pengcheng.fu
 * @date 2019/12/23 14:05
 */

public class BaseVO implements Serializable {

    private static final long serialVersionUID = -7872969928507856739L;

    @AutoDocProperty(value = "【公用参数】请求id", required = true)
    @Length(max = 200, message = "requestId长度不能超过200")
    @NotBlank(message = "【公用参数】requestId不能为空")
    private String requestId;

    @AutoDocProperty(value = "【公用参数】系统来源：当m站在下单前费用计算时传CREDITH5-ALIPAY或FUNDH5-ALIPAY来区分是否显示芝麻免押", hidden = true)
    @Length(max = 200, message = "OS长度不能超过200")
    @JsonProperty(value = "OS")
    private String OS;

    @AutoDocProperty(value = "【公用参数】操作系统版本", hidden = true)
    @Length(max = 200, message = "OsVersion长度不能超过200")
    @JsonProperty(value = "OsVersion")
    private String OsVersion;

    @AutoDocProperty(value = "【公用参数】app应用版本", hidden = true)
    @Length(max = 200, message = "AppVersion长度不能超过200")
    @JsonProperty(value = "AppVersion")
    private String AppVersion;

    @AutoDocProperty(value = "【公用参数】imei", hidden = true)
    @Length(max = 200, message = "IMEI长度不能超过200")
    @JsonProperty(value = "IMEI")
    private String IMEI;

    @AutoDocProperty(value = "【公用参数】oaid", hidden = true)
    @Length(max = 200, message = "oaid长度不能超过200")
    @JsonProperty(value = "OAID")
    private String OAID;

    @AutoDocProperty(value = "【公用参数】经度", hidden = true)
    @Length(max = 20, message = "PublicLongitude长度不能超过50")
    @JsonProperty(value = "PublicLongitude")
    private String PublicLongitude;

    @AutoDocProperty(value = "【公用参数】纬度", hidden = true)
    @Length(max = 20, message = "PublicLatitude长度不能超过50")
    @JsonProperty(value = "PublicLatitude")
    private String PublicLatitude;

    @AutoDocProperty(value = "【公用参数】城市编码", hidden = true)
    @Length(max = 20, message = "publicCityCode长度不能超过20")
    private String publicCityCode;

    @AutoDocProperty(value = "【公用参数】app名称", hidden = true)
    @Length(max = 200, message = "appName长度不能超过200")
    private String appName;

    @AutoDocProperty(value = "【公用参数】设备名称", hidden = true)
    @Length(max = 200, message = "deviceName长度不能超过200")
    private String deviceName;

    @AutoDocProperty(value = "【公用参数】用户token", hidden = true)
    @Length(max = 200, message = "publicToken长度不能超过200")
    private String publicToken;

    @AutoDocProperty(value = "【公用参数】app渠道id", hidden = true)
    @Length(max = 200, message = "AppChannelId长度不能超过200")
    @JsonProperty(value = "AppChannelId")
    private String AppChannelId;

    @AutoDocProperty(value = "【公用参数】mac地址,安卓需要组合mac,IMEI,androidID为唯一标识", hidden = true)
    private String mac;

    @AutoDocProperty(value = "【公用参数】安卓id,安卓需要组合mac,IMEI,androidID为唯一标识", hidden = true)
    private String androidID;

    @AutoDocProperty(value = "【公用参数】用户注册号,拦截器里注入", hidden = true)
    private Integer memNo;

    @JsonProperty(value = "schema")
    @AutoDocProperty(value = "【公用参数】,abTest分类，A或B", hidden = true)
    private String schema;


    public String getRequestId() {
        //请求id为空时，自动生成UUID给日志使用
        if (StringUtils.isBlank(requestId)) {
            this.requestId = MDC.get("request_id");
            if (StringUtils.isBlank(requestId)) {
                this.requestId = UUID.randomUUID().toString();
            }
        }
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public String getOsVersion() {
        return OsVersion;
    }

    public void setOsVersion(String osVersion) {
        OsVersion = osVersion;
    }

    public String getAppVersion() {
        return AppVersion;
    }

    public void setAppVersion(String appVersion) {
        AppVersion = appVersion;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getOAID() {
        return OAID;
    }

    public void setOAID(String OAID) {
        this.OAID = OAID;
    }

    public String getPublicLongitude() {
        return PublicLongitude;
    }

    public void setPublicLongitude(String publicLongitude) {
        PublicLongitude = publicLongitude;
    }

    public String getPublicLatitude() {
        return PublicLatitude;
    }

    public void setPublicLatitude(String publicLatitude) {
        PublicLatitude = publicLatitude;
    }

    public String getPublicCityCode() {
        return publicCityCode;
    }

    public void setPublicCityCode(String publicCityCode) {
        this.publicCityCode = publicCityCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getPublicToken() {
        return publicToken;
    }

    public void setPublicToken(String publicToken) {
        this.publicToken = publicToken;
    }

    public String getAppChannelId() {
        return AppChannelId;
    }

    public void setAppChannelId(String appChannelId) {
        AppChannelId = appChannelId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getAndroidID() {
        return androidID;
    }

    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }

    public Integer getMemNo() {
        return memNo;
    }

    public void setMemNo(Integer memNo) {
        this.memNo = memNo;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        return "BaseVO{" +
                "requestId='" + requestId + '\'' +
                ", OS='" + OS + '\'' +
                ", OsVersion='" + OsVersion + '\'' +
                ", AppVersion='" + AppVersion + '\'' +
                ", IMEI='" + IMEI + '\'' +
                ", OAID='" + OAID + '\'' +
                ", PublicLongitude='" + PublicLongitude + '\'' +
                ", PublicLatitude='" + PublicLatitude + '\'' +
                ", publicCityCode='" + publicCityCode + '\'' +
                ", appName='" + appName + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", publicToken='" + publicToken + '\'' +
                ", AppChannelId='" + AppChannelId + '\'' +
                ", mac='" + mac + '\'' +
                ", androidID='" + androidID + '\'' +
                ", memNo=" + memNo +
                ", schema='" + schema + '\'' +
                '}';
    }
}
