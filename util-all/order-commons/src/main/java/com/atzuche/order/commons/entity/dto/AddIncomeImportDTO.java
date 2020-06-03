package com.atzuche.order.commons.entity.dto;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddIncomeImportDTO {

	private AddIncomeExcelEntity addIncomeExcel;
	private List<AddIncomeExcelContextEntity> contentList;
}
