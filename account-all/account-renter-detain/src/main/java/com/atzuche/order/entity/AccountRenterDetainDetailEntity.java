package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 暂扣费用进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:51:17
 * @Description:
 */
@Data
public class AccountRenterDetainDetailEntity implements Serializable {
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
	 * 会员号
	 */
	private Integer memNo;
	/**
	 * 暂扣金额
	 */
	private Integer amt;
	/**
	 * 暂扣时间
	 */
	private LocalDateTime time;
	/**
	 * 暂扣费用来源编码
	 */
	private Integer sourceCode;
	/**
	 * 暂扣费用来源编码描述
	 */
	private String sourceDetail;
	/**
	 * 暂扣凭证
	 */
	private String uniqueNo;
	/**
	 * 备注
	 */
	private String remake;
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
