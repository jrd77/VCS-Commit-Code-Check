package com.atzuche.order.commons.entity.dto;

import javax.validation.constraints.NotNull;


import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddIncomeExcelOptDTO {

	@AutoDocProperty(value = "追加收益id")
	@NotNull(message="追加收益id不能为空")
	private Integer id;
	@AutoDocProperty(value = "操作：1-审核通过，3-撤回")
	@NotNull(message="操作类型不能为空")
	private Integer flag;
	// 操作人
	private String operator;
}
