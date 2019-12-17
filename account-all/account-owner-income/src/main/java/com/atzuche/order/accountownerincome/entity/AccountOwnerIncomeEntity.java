package com.atzuche.order.accountownerincome.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 车主收益总表
 * 
 * @author ZhangBin
 * @date 2019-12-17 16:44:06
 * @Description:
 */
@Data
public class AccountOwnerIncomeEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 收益总金额
	 */
	private Integer incomeAmt;
	/**
	 * 更新版本号
	 */
	private Integer version;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

}
