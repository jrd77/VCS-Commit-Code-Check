package com.atzuche.order.renterwz.entity;

import lombok.Data;

import java.util.Date;

/**
 * RenterOrderWzEverydayTodoEntity
 *
 * @author shisong
 * @date 2019/12/28
 */
@Data
public class RenterOrderWzEverydayTodoEntity{
	/**
	*主键
	*/
	private Long id;
	/**
	*订单编号
	*/
	private String orderNo;
	/**
	*车辆编号
	*/
	private Integer carNo;
	/**
	*车牌
	*/
	private String carPlateNum;
	/**
	*租车时间
	*/
	private Date rentTime;
	/**
	*还车时间
	*/
	private Date revertTime;
	/**
	*租客编号
	*/
	private Integer renterNo;
	/**
	*租客手机号
	*/
	private String renterPhone;
	/**
	*车架号
	*/
	private String frameNo;
	/**
	*发动机号
	*/
	private String engineNum;
	/**
	*是否查询：0否 1是
	*/
	private Boolean status;
	/**
	*车辆所属城市名称
	*/
	private String cityName;
	/**
	*车辆订单内途径城市
	*/
	private String cities;
	/**
	*创建人
	*/
	private String createOp;
	/**
	*创建时间
	*/
	private Date createTime;
	/**
	*修改时间
	*/
	private Date updateTime;
	/**
	*修改人
	*/
	private String updateOp;
	/**
	*是否删除 0未删除 1已删除
	*/
	private Integer isDelete;
}
