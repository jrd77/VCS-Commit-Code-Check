package com.atzuche.order.commons.entity.dto;


import com.atzuche.order.commons.PageBean;
import com.autoyol.doc.annotation.AutoDocProperty;

public class AddIncomeExcelQueryDTO extends PageBean{

	private static final long serialVersionUID = 667215303399140607L;
	
	public AddIncomeExcelQueryDTO(long pageNumber,long total,long pageSize) {
		super(pageNumber, total, pageSize);
	}
	@AutoDocProperty(value = "文件名")
    private String fileName;

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
