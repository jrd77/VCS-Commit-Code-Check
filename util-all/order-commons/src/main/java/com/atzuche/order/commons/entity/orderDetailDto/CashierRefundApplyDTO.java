package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 退款申请表
 * 
 * @author ZhangBin
 * @date 2019-12-26 14:12:04
 * @Description:
 */
@ToString
@Data
public class CashierRefundApplyDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
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
	 * 退款类型来源code
	 */
	private String sourceCode;
	/**
	 * 退款类型来源描述
	 */
	private String sourceDetail;
	/**
	 * 凹凸appID
	 */
	private String atappId;
	/**
	 * 支付项目
	 */
	private String payKind;
	/**
	 * 支付类型
	 */
	private String payType;
	/**
	 * 支付来源
	 */
	private String paySource;
	/**
	 * 退款金额
	 */
	private Integer amt;
	/**
	 * 退款状态
	 */
	private String status;
	/**
	 * 支付流水号
	 */
	private String qn;
	/**
	 * 退款外部凭证
	 */
	private String uniqueNo;
	/**
	 * 退款项目：押金，违章押金，费用
	 */
	private Integer flag;
	/**
	 * 类型（系统退款/手工退款）
	 */
	private Integer type;
	/**
	 * 退款发起次数
	 */
	private Integer num;
	/**
	 * 
	 */
	private String payMd5;
	/**
	 * 备注
	 */
	private String remake;
	/**
	 * 操作部门
	 */
	private String deptName;
	/**
	 * 
	 */
	private LocalDateTime createTime;
	/**
	 * 
	 */
	private String createOp;
	/**
	 * 
	 */
	private LocalDateTime updateTime;
	/**
	 * 
	 */
	private String updateOp;
	/**
	 * 
	 */
	private Integer version;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

    /**
     *退款成功时间
     */
    private LocalDateTime refundTime;

}
