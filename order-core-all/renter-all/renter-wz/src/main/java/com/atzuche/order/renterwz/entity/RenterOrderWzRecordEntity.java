package com.atzuche.order.renterwz.entity;

import lombok.Data;

import java.util.Date;

/**
 * RenterOrderWzRecordEntity
 *
 * @author shisong
 * @date 2019/12/28
 */
@Data
public class RenterOrderWzRecordEntity{
	/**
	*主键
	*/
	private Long id;
	/**
	*车牌
	*/
	private String carPlateNum;
	/**
	*返回json内容
	*/
	private String resultJson;
	/**
	*查询日期
	*/
	private Date createTime;
}
