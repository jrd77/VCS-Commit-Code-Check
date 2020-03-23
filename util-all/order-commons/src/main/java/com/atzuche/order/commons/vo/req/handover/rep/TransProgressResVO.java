package com.atzuche.order.commons.vo.req.handover.rep;

import java.io.Serializable;
import java.util.Date;

/**
 * 取送车进度
 * add by shengjie
 * 2016年6月28日10:55:03
 */
public class TransProgressResVO implements Serializable{

	private static final long serialVersionUID = -3711690125418975651L;

	private String description;
	
	private String handleTime;

	private Date createTime;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHandleTime() {
		return handleTime;
	}

	public void setHandleTime(String handleTime) {
		this.handleTime = handleTime;
	}


	
}
