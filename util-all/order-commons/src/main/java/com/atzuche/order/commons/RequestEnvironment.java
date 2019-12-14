package com.atzuche.order.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

/**
 * 请求环境信息
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/14 2:50 下午
 **/
public class RequestEnvironment {
    //手机操作系统，因为手机端是大写，所以配合传递参数大写

    @Length(max=200,message="OS长度不能超过200")
    @JsonProperty(value="OS")
    @ApiModelProperty(value="【5.10修改公用参数】系统来源：当m站在下单前费用计算时传CREDITH5-ALIPAY或FUNDH5-ALIPAY来区分是否显示芝麻免押",hidden=true)
    private String OS;


    //操作系统版本

    @Length(max=200,message="OsVersion长度不能超过200")
    @JsonProperty(value="OsVersion")
    @ApiModelProperty(value="【公用参数】操作系统版本",hidden=true)
    private String OsVersion;


    //APP应用版本

    @Length(max=200,message="AppVersion长度不能超过200")
    @JsonProperty(value="AppVersion")
    @ApiModelProperty(value="【公用参数】app应用版本",hidden=true)
    private String AppVersion;

    //安卓手机IMEI

    @Length(max=200,message="AppVersion长度不能超过200")
    @JsonProperty(value="IMEI")
    @ApiModelProperty(value="【公用参数】imei",hidden=true)
    private String IMEI;


    //经度
    @ApiModelProperty(value="【公用参数】经度",hidden=true)
    @Length(max=20,message="PublicLongitude长度不能超过50")
    @JsonProperty(value="PublicLongitude")
    private String PublicLongitude;




    //纬度
    @ApiModelProperty(value="【公用参数】纬度",hidden=true)
    @Length(max=20,message="PublicLatitude长度不能超过50")
    @JsonProperty(value="PublicLatitude")
    private String PublicLatitude;


    //城市手机区号
    @ApiModelProperty(value="【公用参数】城市编码",hidden=true)
    @Length(max=20,message="publicCityCode长度不能超过20")
    private String publicCityCode;


    //app名称
    @ApiModelProperty(value="【公用参数】app名称",hidden=true)
    @Length(max=200,message="appName长度不能超过200")
    private String appName;


    //设备名称
    @ApiModelProperty(value="【公用参数】设备名称",hidden=true)
    @Length(max=200,message="deviceName长度不能超过200")
    private String deviceName;


    //用户token,公共参数token
    @ApiModelProperty(value="【公用参数】用户token",hidden=true)
    @Length(max=200,message="publicToken长度不能超过200")
    private String publicToken;


    //app渠道id

    @Length(max=200,message="AppChannelId长度不能超过200")
    @JsonProperty(value="AppChannelId")
    @ApiModelProperty(value="【公用参数】app渠道id",hidden=true)
    private String AppChannelId;

    //mac地址
    @ApiModelProperty(value="【公用参数,4.7新增】mac地址,安卓需要组合mac,IMEI,androidID为唯一标识",hidden=true)
    private String mac;

    //安卓id
    @ApiModelProperty(value="【公用参数,4.7新增】安卓id,安卓需要组合mac,IMEI,androidID为唯一标识",hidden=true)
    private String androidID;

    @ApiModelProperty(value="请求IP地址",hidden=true)
    private String ip;
    @ApiModelProperty(value="请求IP port",hidden=true)
    private int port;


}
