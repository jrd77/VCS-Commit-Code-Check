package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 理赔费用/及其结算总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:47:25
 * @Description:
 */
@Data
public class AccountRenterClaimCostSettleEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private Long orderNo;
	/**
	 * 会员号
	 */
	private Integer memNo;
	/**
	 * 理赔状态
	 */
	private Integer status;
	/**
	 * 应付理赔金额
	 */
	private Integer yingfuAmt;
	/**
	 * 实付理赔金额
	 */
	private Integer shifuAmt;
	/**
	 * 欠理赔款金额
	 */
	private Integer qiankuanAmt;
	/**
	 * 更新版本号
	 */
	private Integer version;
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
	 * 更新人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
