package com.atzuche.order.admin.common;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author zg
 * @Date 2013-9-8
 * @Comments 分页计算Bean（for MySql,Mybatis）
 */
@Data
@ToString
public class PageBean implements Serializable{
	
	private long total;	// 总记录数
	private long pages; //一共多少页
	private long pageNumber = 1;  //当前页码数 (默认为1)
	private long pageSize;	  //每页显示记录数
	private long startIndex;   //记录开始位置

	public static final long PAGE_SIZE = 10;
	
	/**
	 * @param curPageNum -当前页码数 (默认为1)
	 * @param total -总记录数
	 * @param pageSize -每页显示记录数
	 */
	public PageBean(long curPageNum, long total, long pageSize){
		this.setCurPageNum(curPageNum);
		this.setPageSize(pageSize);
		if(-1 != total) {
			this.setTotal(total);
			this.setTotalPageNumber();
			//判断当前页面
			if(curPageNum>pages){
				this.setCurPageNum(1);
			}
		}
		this.setStartIndex();
	}
	/**
	 * @param total -总记录数
	 * @param pageSize -每页显示记录数
	 */
	public PageBean(long total, long pageSize){
		this.setTotal(total);
		this.setPageSize(pageSize);
		this.setTotalPageNumber();
		this.setCurPageNum(pageNumber);
		this.setStartIndex();
	}
	



	/**	根据总记录数与每页显示数，计算一共多少页	*/
	public void setTotalPageNumber() {
		if(total < pageSize){
			this.pages = 1;
		}else if(total % pageSize == 0){
			this.pages = total /pageSize ;
		}else{
			this.pages = total /pageSize + 1;
		}
	}

	/** 如果当前页数小于1，则设置当前页数为1 */
	public void setCurPageNum(long pageNumber) {
		if(pageNumber < 1){
			this.pageNumber = 1;
		}/*else if(curPageNum > pages){//不对当前页数和总页数进行判断
			this.curPageNum = pages;
		}*/else{
			this.pageNumber = pageNumber;
		}
	}

	public void setStartIndex() {
		this.startIndex = (pageNumber-1)*pageSize;
	}

}
