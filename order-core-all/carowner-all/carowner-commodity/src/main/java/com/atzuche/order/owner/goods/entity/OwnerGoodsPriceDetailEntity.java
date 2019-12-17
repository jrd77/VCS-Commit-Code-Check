package com.atzuche.order.owner.goods.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 车主端商品概览价格明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:12:50
 * @Description:
 */
@Data
public class OwnerGoodsPriceDetailEntity implements Serializable {
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
	private String ownerOrderNo;
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
