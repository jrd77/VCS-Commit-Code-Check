package com.atzuche.order.commons.entity.dto;

import lombok.Data;

@Data
public class OwnerMemberRightDTO {
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 子订单号
	 */
	private String ownerOrderNo;
    /**
     * 权益编码
     */
    private String rightCode;
    /**
     * 权益名称（会员等级、是否内部员工、vip等）
     */
    private String rightName;
    /**
     * 权益值
     */
    private String rightValue;
    /**
     * 权益描述
     */
    private String rightDesc;
    /**
     * 权益类别
     */
    private Integer rightType;
}
