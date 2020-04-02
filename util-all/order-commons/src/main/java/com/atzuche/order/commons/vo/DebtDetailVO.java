package com.atzuche.order.commons.vo;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DebtDetailVO {

	@AutoDocProperty(value = "会员历史欠款")
	private Integer historyDebtAmt;
	@AutoDocProperty(value = "会员订单欠款")
	private Integer orderDebtAmt;
}
