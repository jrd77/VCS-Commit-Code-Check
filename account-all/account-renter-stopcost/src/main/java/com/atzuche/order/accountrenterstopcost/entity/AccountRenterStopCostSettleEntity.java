package com.atzuche.order.accountrenterstopcost.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 停运费状态及其结算总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:54:29
 * @Description:
 */
@Data
public class AccountRenterStopCostSettleEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键
	 */
	private Integer id;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 应付金额
	 */
	private Integer yingfuAmt;
	/**
	 * 实付金额
	 */
	private Integer shifuAmt;
	/**
	 * 欠款金额
	 */
	private Integer amt;
	/**
	 * 结算状态
	 */
	private Integer settleStatus;
	/**
	 * 更新版本号
	 */
	private Integer version;
	/**
	 * 修改时间
	 */
	private LocalDateTime updateTime;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;
	/**
	 * 更新人
	 */
	private String updateOp;
	/**
	 * 创建人
	 */
	private String createOp;

}
