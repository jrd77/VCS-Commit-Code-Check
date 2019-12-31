package com.atzuche.order.renterwz.entity;

import lombok.Data;

import java.util.Date;

/**
 * RenterOrderWzQueryRecordEntity
 *
 * @author shisong
 * @date 2019/12/28
 */
@Data
public class RenterOrderWzQueryRecordEntity{
	/**
	*主键
	*/
	private Long id;
	/**
	*订单号
	*/
	private String orderNo;
	/**
	*车牌
	*/
	private String carPlateNum;
	/**
	*返回内容JSON
	*/
	private String resultJson;
	/**
	*返回码
	*/
	private String returnCode;
	/**
	*错误信息
	*/
	private String errorMsg;
	/**
	*违章标识:0无 1有
	*/
	private Integer illegalFlag;
	/**
	*查询城市
	*/
	private String city;
	/**
	*创建时间
	*/
	private Date createTime;
	/**
	*修改时间
	*/
	private Date updateTime;
}
