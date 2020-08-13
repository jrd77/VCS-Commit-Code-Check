package com.atzuche.order.accountownerincome.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 车主收益总表
 * 
 * @author ZhangBin
 * @date 2019-12-17 16:44:06
 */
@Data
public class AccountOwnerIncomeEntity implements Serializable {

    private static final long serialVersionUID = 6456038683974201953L;

    /**
	 * 主键
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
     * 上海二清收益金额(可提现)
     */
	private Integer secondaryIncomeAmt;

    /**
     * 上海二清收益金额(冻结不可提现)
     */
    private Integer secondaryFreezeIncomeAmt;

	/**
	 * 更新版本号
	 */
	private Integer version;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

}
