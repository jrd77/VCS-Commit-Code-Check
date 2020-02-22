package com.atzuche.config.common.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 
 * 
 * @author zhangbin
 * @date 2020-02-21 11:36:28
 * @Description:
 */
@Data
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

}
