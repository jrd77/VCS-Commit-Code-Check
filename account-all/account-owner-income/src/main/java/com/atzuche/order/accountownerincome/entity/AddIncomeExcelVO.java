package com.atzuche.order.accountownerincome.entity;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddIncomeExcelVO {

	private List<AddIncomeExcelEntity> list;
	private Integer totalSize;
	
}
