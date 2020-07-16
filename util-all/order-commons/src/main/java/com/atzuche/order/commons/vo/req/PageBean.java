package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 分页对象
 */
public class PageBean implements Serializable {

	private static final long serialVersionUID = 6277947143171115766L;

	@AutoDocProperty(value = "页码")
	@NotNull(message="页码不允许为空")
	private Integer pageNum;
	@AutoDocProperty(value = "每页大小")
	private Integer pageSize = 10;//默认10

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}
