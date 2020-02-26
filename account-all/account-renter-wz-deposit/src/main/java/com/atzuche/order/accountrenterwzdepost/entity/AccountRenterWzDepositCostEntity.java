package com.atzuche.order.accountrenterwzdepost.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 违章费用总表及其结算总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 * @Description:
 */
@Data
public class AccountRenterWzDepositCostEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
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
	 * 应付违章费用
	 */
	private Integer yingfuAmt;
	/**
	 * 实付违章费用
	 */
	private Integer shifuAmt;
	/**
	 * 违章欠款金额
	 */
	private Integer debtAmt;
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
	 * 更新时间
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
