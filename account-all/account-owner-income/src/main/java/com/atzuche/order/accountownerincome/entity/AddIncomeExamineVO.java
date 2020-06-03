package com.atzuche.order.accountownerincome.entity;

import java.util.List;

import lombok.Data;
import lombok.ToString;
@Data
@ToString
public class AddIncomeExamineVO {

	private List<AddIncomeExamine> list;
	private Integer totalSize;
}
