package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddIncomeExcelOptDTO {

	@AutoDocProperty(value = "追加收益id")
	private Integer id;
	@AutoDocProperty(value = "操作：1-审核通过，3-撤回")
	private Integer flag;
	// 操作人
	private String operator;
}
