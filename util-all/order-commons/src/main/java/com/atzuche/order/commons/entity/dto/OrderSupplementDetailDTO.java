package com.atzuche.order.commons.entity.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderSupplementDetailDTO {

	/**
	 * 订单号
	 */
	@NotBlank(message="订单编号不能为空")
	private String orderNo;
	
	/**
	 * 单项补付金额
	 */
	@NotNull(message="单项补付金额不能为空")
	private Integer amt;
	
	/**
	 * 费用编码
	 */
	@NotBlank(message="费用编码不能为空")
	private String cashNo;
	
	/**
	 * 补付名称
	 */
	private String title;
	
	/**
	 * 操作状态:0,待提交 1,已生效 2,已失效 3,已撤回
	 */
	private Integer opStatus;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 支付状态:0.无需支付 1.未支付 2.已取消 3.已支付 4.支付中，5.支付失败 10.租车押金结算抵扣  20.违章押金结算抵扣
	 */
	private Integer payFlag;
}
