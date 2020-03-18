package com.atzuche.violation.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * RenterOrderWzDetailEntity
 *
 * @author shisong
 * @date 2019/12/28
 */
@Data
@ToString
public class RenterOrderWzDetailEntity{
	/**
	*主键
	*/
	private Long id;
	/**
	*主订单号
	*/
	private String orderNo;
	/**
	*车牌
	*/
	private String carPlateNum;
	/**
	*订单内:0否 1是
	*/
	private Integer orderFlag;
	/**
	*违章时间
	*/
	private Date illegalTime;
	/**
	*违章地点
	*/
	private String illegalAddr;
	/**
	*违章原因
	*/
	private String illegalReason;
	/**
	*违章罚款
	*/
	private String illegalAmt;
	/**
	*违章扣分
	*/
	private String illegalDeduct;
	/**
	*渠道处理费用（协助违章处理费）
	*/
	private Integer illegalFine;
	/**
	*不良用车处罚金
	*/
	private Integer illegalDysFine;
	/**
	*服务费（凹凸代办服务费）
	*/
	private Integer illegalServiceCost;
	/**
	*是否有效:0,否；1，是
	*/
	private Integer isValid;
	/**
	*是否有发送过短信:0,否；1，是
	*/
	private Integer isSms;
	/**
	*违章处理状态：0-未处理，1-已处理
	*/
	private Integer illegalStatus;
	/**
	*违章编码,唯一，非违章条例码(典典)
	*/
	private String code;
	/**
	*违章信息唯一标示（仁云）
	*/
	private String ryWzCode;
	/**
	*车主发送短信标识
	*/
	private Integer ownerIsSms;
	/**
	*违章超证费
	*/
	private Integer illegalSupercerCost;
	/**
	*删除人
	*/
	private String deleteOperate;
	/**
	*删除时间
	*/
	private Date deleteTime;
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
