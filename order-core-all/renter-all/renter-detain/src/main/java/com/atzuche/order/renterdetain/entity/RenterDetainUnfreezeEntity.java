package com.atzuche.order.renterdetain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 暂扣解冻表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:13:12
 * @Description:
 */
@Data
public class RenterDetainUnfreezeEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 暂扣事件id
	 */
	private Integer renterEventDetainId;
	/**
	 * 解冻金额
	 */
	private Integer unfreezeAmt;
	/**
	 * 解冻原因
	 */
	private String reason;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 创建人/解冻人
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
