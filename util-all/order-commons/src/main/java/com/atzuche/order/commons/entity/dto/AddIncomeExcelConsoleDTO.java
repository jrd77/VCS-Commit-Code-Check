package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddIncomeExcelConsoleDTO {

	@AutoDocProperty(value = "文件名")
    private String fileName; 
	@AutoDocProperty("当前页码")
    private String pageNumber;
	@AutoDocProperty("一页大小")
    private String pageSize;
}
