/**
 * 
 */
package com.atzuche.order.commons.vo;

import java.util.List;

/**
 * @author jing.huang
 *
 */
public class SupplementCheckReqVO {
	private String memNo;
	private List<String> orderNoList;
	
	public String getMemNo() {
		return memNo;
	}
	public void setMemNo(String memNo) {
		this.memNo = memNo;
	}
	public List<String> getOrderNoList() {
		return orderNoList;
	}
	public void setOrderNoList(List<String> orderNoList) {
		this.orderNoList = orderNoList;
	}
	
}
