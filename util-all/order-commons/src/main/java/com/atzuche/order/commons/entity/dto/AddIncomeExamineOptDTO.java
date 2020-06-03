package com.atzuche.order.commons.entity.dto;

import javax.validation.constraints.NotNull;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddIncomeExamineOptDTO {
	@AutoDocProperty(value = "收益审核记录id")
	@NotNull(message="收益审核记录id不能为空")
	private Integer id;
	@AutoDocProperty(value = "审核结果：1-审核通过，2-审核不通过，3-审核中")
	@NotNull(message="审核结果不能为空")
	private Integer flag;
	@AutoDocProperty(value = "待审核原因：3-审核中，待核查（异常），4-审核中，待核查（测试），5-其他")
	private Integer waitFlag;
	@AutoDocProperty(value = "备注")
	private String remark;
	private String operator;
}
