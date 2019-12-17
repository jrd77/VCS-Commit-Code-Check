package com.atzuche.order.rentercommodity.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * 商品概览价格明细表
 * 
 * @author ZhangBin
 * @date 2019-12-17 11:43:15
 * @Description:
 */
@Data
public class RenterGoodsPriceDetailEntity implements Serializable {
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
	private Float carHourCount;
	/**
	 * 分组id
	 */
	private LocalDateTime revertTime;
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
