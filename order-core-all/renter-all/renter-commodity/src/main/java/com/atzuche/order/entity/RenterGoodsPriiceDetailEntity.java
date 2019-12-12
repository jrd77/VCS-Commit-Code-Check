package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 商品概览价格明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:06:32
 * @Description:
 */
@Data
public class RenterGoodsPriiceDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private Integer orderNo;
	/**
	 * 子订单号
	 */
	private String renterOrderNo;
	/**
	 * 商品概览id
	 */
	private Integer goodsId;
	/**
	 * 天
	 */
	private LocalDate carDay;
	/**
	 * 天单价
	 */
	private Integer carUnitPrice;
	/**
	 * 小时数
	 */
	private Integer carHourCount;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 创建人
	 */
	private String createOp;
	/**
	 * 修改时间
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
