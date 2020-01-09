package com.atzuche.order.renterwz.entity;

import lombok.Data;

import java.util.Date;

/**
 * DerenCarApproachCitysEntity
 *
 * @author shisong
 * @date 2020/01/08
 */
@Data
public class DerenCarApproachCitysEntity{
	private Long id;
	/**
	*车牌号
	*/
	private String plateNum;
	/**
	*gps设备号
	*/
	private String simNo;
	/**
	*订单号
	*/
	private Long orderNo;
	/**
	*会员号
	*/
	private Long memNo;
	/**
	*起租时间(yyyyMMddHHmmss)
	*/
	private String startTime;
	/**
	*结束时间(yyyyMMddHHmmss)
	*/
	private String endTime;
	/**
	*途径城市,格式,如:["上海","北京","深圳"]
	*/
	private String cities;
	/**
	*创建时间
	*/
	private Date createTime;
	/**
	*更新时间
	*/
	private Date updateTime;
}
