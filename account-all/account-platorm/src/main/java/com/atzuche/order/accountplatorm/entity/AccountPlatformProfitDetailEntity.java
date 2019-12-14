package com.atzuche.order.accountplatorm.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 平台结算收益明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:45:24
 * @Description:
 */
@Data
public class AccountPlatformProfitDetailEntity implements Serializable {
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
	 * 费用
	 */
	private Integer amt;
	/**
	 * 费用来源编码
	 */
	private String sourceCode;
	/**
	 * 费用来源描述
	 */
	private String sourceDesc;
	/**
	 * 费用产生凭证
	 */
	private String uniqueNo;
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
