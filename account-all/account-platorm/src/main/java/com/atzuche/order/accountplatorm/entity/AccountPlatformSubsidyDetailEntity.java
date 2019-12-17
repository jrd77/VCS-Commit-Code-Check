package com.atzuche.order.accountplatorm.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 平台结算补贴明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:45:24
 * @Description:
 */
@Data
public class AccountPlatformSubsidyDetailEntity implements Serializable {
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
	 * 费用
	 */
	private Integer amt;
	/**
	 * 补贴科目来源编码
	 */
	private Integer sourceCode;
	/**
	 * 补贴科目来源描述
	 */
	private String sourceDesc;
	/**
	 * 补贴产生凭证
	 */
	private String uniqueNo;
	/**
	 * 补贴方（车主/租客）
	 */
	private String subsidyName;
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
