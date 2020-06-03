package com.atzuche.order.commons.entity.dto;

import java.util.List;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddIncomeExcelVO {
	@AutoDocProperty(value = "收益列表")
	private List<AddIncomeExcelEntity> list;
	@AutoDocProperty(value = "总记录数")
	private Integer totalSize;
	
}
