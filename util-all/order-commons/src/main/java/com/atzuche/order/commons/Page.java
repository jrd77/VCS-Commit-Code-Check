package com.atzuche.order.commons;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 分页对象
 *
 * @author chuanhong.liu
 */
public class Page<T> implements Serializable {

	private static final long serialVersionUID = 2019028633247496038L;

	@AutoDocProperty(value = "当前页码")
	private int pageNum;
	@AutoDocProperty(value = "每页大小")
	private int pageSize;
	@AutoDocProperty(value = "总页数")
	private long total;
	@AutoDocProperty(value = "结果集")
	private List<T> list;


	public Page() {
	}

	/**
	 * 包装Page对象
	 *
	 * @param list
	 *            page结果
	 * @param navigatePages
	 *            页码数量
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Page(List<T> list) {
		if (list instanceof com.github.pagehelper.Page) {
			com.github.pagehelper.Page page = (com.github.pagehelper.Page) list;
			this.pageNum = page.getPageNum();
			this.pageSize = page.getPageSize();
			this.list = page;
			this.total = page.getTotal();
		} else if (list instanceof Collection) {
			this.pageNum = 1;
			this.pageSize = list.size();
			this.list = list;
			this.total = list.size();
		}
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}


	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("PageInfo{");
		sb.append("pageNum=").append(pageNum);
		sb.append(", pageSize=").append(pageSize);
		sb.append(", total=").append(total);
		sb.append(", list=").append(list);
		sb.append('}');
		return sb.toString();
	}
}
