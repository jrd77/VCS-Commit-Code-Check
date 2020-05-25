package com.atzuche.order.commons.entity.dto;


import com.atzuche.order.commons.PageBean;
import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddIncomeExcelQueryDTO extends PageBean{

	public AddIncomeExcelQueryDTO(long pageNumber,long total,long pageSize) {
		super(pageNumber, total, pageSize);
	}
	@AutoDocProperty(value = "文件名")
    private String fileName;
}
