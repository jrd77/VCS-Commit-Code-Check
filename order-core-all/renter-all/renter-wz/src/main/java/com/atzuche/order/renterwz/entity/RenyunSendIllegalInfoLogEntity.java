package com.atzuche.order.renterwz.entity;

import lombok.Data;

import java.util.Date;

/**
 * RenyunSendIllegalInfoLogEntity
 *
 * @author shisong
 * @date 2019/12/28
 */
@Data
public class RenyunSendIllegalInfoLogEntity{
	/**
	*主键
	*/
	private Long id;
	/**
	*违章单据唯一标示
	*/
	private String wzcode;
	/**
	*主订单号
	*/
	private String orderno;
	/**
	*车牌
	*/
	private String carPlateNum;
	/**
	*01-违章信息，02-违章处理报价信息，03-仁云流程系统反馈租客凭证上传信息，04-仁云流程系统反馈订单车辆违章处理方信息
	*/
	private String dataType;
	/**
	*JSON格式整个报文
	*/
	private String jsonData;
	/**
	*创建时间
	*/
	private Date createTime;
	/**
	*修改时间
	*/
	private Date updateTime;
	/**
	*是否删除 0未删除 1已删除
	*/
	private Integer isDelete;
}
