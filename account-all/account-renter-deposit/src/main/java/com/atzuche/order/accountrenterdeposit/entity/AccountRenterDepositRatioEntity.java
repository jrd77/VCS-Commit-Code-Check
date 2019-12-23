package com.atzuche.order.accountrenterdeposit.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 车辆押金减免明细表
 * 
 * @author ZhangBin
 * @date 2019-12-20 11:58:57
 * @Description:
 */
@Data
public class AccountRenterDepositRatioEntity implements Serializable {
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
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 减免金额
	 */
	private Integer amt;
	/**
	 * 减免类型
	 */
	private Integer type;
	/**
	 * 减免类型名称
	 */
	private String typeName;
	/**
	 * 减免比例
	 */
	private Integer ratio;
	/**
	 * 减免项名称
	 */
	private String name;
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
	 * 更新人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
