package com.atzuche.order.parentorder.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 下单记录表
 * 
 * @author ZhangBin
 * @date 2020-01-01 15:06:16
 * @Description:
 */
@Data
public class OrderRecordEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 下单时的入参
	 */
	private String param;
	/**
	 * 下单结束产生结果
	 */
	private String result;
	/**
	 * errorCode枚举对应的code
	 */
	private String errorCode;
	/**
	 * errorCode枚举对应的txt
	 */
	private String errorTxt;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 创建人
	 */
	private String createOp;
	/**
	 * 更新时间
	 */
	private LocalDateTime updateTime;
	/**
	 * 修改人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
