package com.atzuche.order.accountownerincome.entity;

import java.util.List;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
@Data
@ToString
public class AddIncomeExamineVO {
	@AutoDocProperty(value = "收益审核列表")
	private List<AddIncomeExamine> list;
	@AutoDocProperty(value = "总记录数")
	private Integer totalSize;
}
