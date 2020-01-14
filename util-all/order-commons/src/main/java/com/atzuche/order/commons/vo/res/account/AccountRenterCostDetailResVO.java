package com.atzuche.order.commons.vo.res.account;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;


/**
 * 租车费用资金进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-25 20:37:23
 * @Description:
 */
@Data
public class AccountRenterCostDetailResVO implements Serializable {
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
	 * 会员号
	 */
	private String memNo;
	/**
	 * 支付来源code
	 */
	private String paySourceCode;
	/**
	 * 支付来源
	 */
	private String paySource;
	/**
	 * 支付渠道code
	 */
	private String payTypeCode;
	/**
	 * 支付渠道
	 */
	private String payType;
	/**
	 * 入账金额
	 */
	private Integer amt;
	/**
	 * 入账来源编码
	 */
	private String sourceCode;
	/**
	 * 入账来源编码描述
	 */
	private String sourceDetail;
	/**
	 * 交易时间
	 */
//	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime transTime;
	/**
	 * 唯一标识
	 */
	private String uniqueNo;
	/**
	 * 入账时间
	 */
//	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime time;
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
