package com.atzuche.order.renterwz.entity;

import lombok.Data;

import java.util.Date;

/**
 * RenterOrderWzIllegalPhotoEntity
 *
 * @author shisong
 * @date 2019/12/28
 */
@Data
public class RenterOrderWzIllegalPhotoEntity{
	/**
	*主键
	*/
	private Long id;
	/**
	*存储路径
	*/
	private String path;
	/**
	*1:租客，2:车主,3:平台
	*/
	private Integer userType;
	/**
	*主订单号
	*/
	private String orderNo;
	/**
	*车牌
	*/
	private String carPlateNum;
	/**
	*图片序列号
	*/
	private Integer serialNumber;
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
