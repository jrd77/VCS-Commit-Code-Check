package com.atzuche.order.coreapi.entity.vo.res;

import lombok.Data;

import java.util.Date;

@Data
public class TransIllegalDetailResVO{

	private String orderNo;
	private Date illegalTime;
	private String illegalAddr;
	private String illegalReason;
	private Integer illegalAmt;
	private Integer illegalDeduct;
	private Date createTime;
	private Integer orderFlag;
	private Integer illegalStatus;
	private Date updateTime;

}
