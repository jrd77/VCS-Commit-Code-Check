package com.atzuche.violation.entity;

import lombok.Data;

import java.util.Date;

/**
 * RenterOrderWzSettleFlagEntity
 *
 * @author shisong
 * @date 2019/12/28
 */
@Data
public class RenterOrderWzSettleFlagEntity {
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
	*是否有违章：1-未通知，2-有违章，3-无违章
	*/
	private Integer hasIllegal;
	/**
	*是否有违章扣款金额：0-无，1-有
	*/
	private Integer hasIllegalCost;
	/**
	*违章结算标示，0-未结算，1-已结算-成功，2-已结算失败
	*/
	private Integer settleFlag;
	/**
	*结算标示变更时间
	*/
	private Date settleTime;
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
