package com.atzuche.order.renterwz.entity;

import lombok.Data;

import java.util.Date;

/**
 * RenterOrderWzCostDetailEntity
 *
 * @author shisong
 * @date 2019/12/28
 */
@Data
public class RenterOrderWzCostDetailEntity{
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
	*会员号
	*/
	private Integer memNo;
	/**
	*费用编码
	*/
	private String costCode;
	/**
	*费用描述
	*/
	private String costDesc;
	/**
	*总价
	*/
	private Integer amount;
	/**
	*备注信息
	*/
	private String note;
	/**
	*来源信息：1、仁云、2、管理后台
	*/
	private String sourceType;
	/**
	*操作人ID
	*/
	private String operatorId;
	/**
	*操作人名称
	*/
	private String operatorName;
	/**
	*0-正常，1-失效,管理后台修改费用时会造成同类费用上一笔失效
	*/
	private Integer costStatus;
	/**
	*备注
	*/
	private String remark;
	/**
	*创建时间
	*/
	private Date createTime;
	/**
	*创建人
	*/
	private String createOp;
	/**
	*修改时间
	*/
	private Date updateTime;
	/**
	*修改人
	*/
	private String updateOp;
	/**
	*0-正常，1-已逻辑删除
	*/
	private Integer isDelete;
}
