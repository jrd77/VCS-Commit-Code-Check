package com.atzuche.order.commons.entity.dto;

import com.atzuche.order.commons.PageBean;
import com.autoyol.doc.annotation.AutoDocProperty;

public class AddIncomeExamineQueryDTO extends PageBean{

	private static final long serialVersionUID = -2623447977125168110L;
	
	public AddIncomeExamineQueryDTO(long curPageNum, long total, long pageSize) {
		super(curPageNum, total, pageSize);
	}

	@AutoDocProperty(value = "订单号")
	private String orderNo;
	@AutoDocProperty(value = "会员手机号")
	private String mobile;
	@AutoDocProperty(value = "会员号")
	private String memNo;
	@AutoDocProperty(value = "会员姓名")
	private String name;
	@AutoDocProperty(value = "申请部门")
	private String department;
	@AutoDocProperty(value = "审核状态：0-未审核，1-审核通过，2-审核不通过，3-审核中，待核查（异常）,4-审核中，待核查（测试）,5-其他")
	private Integer status;
	@AutoDocProperty(value = "创建时间开始，格式：yyyy-MM-dd HH:mm:ss")
	private String createTimeBegin;
	@AutoDocProperty(value = "创建时间结束，格式：yyyy-MM-dd HH:mm:ss")
	private String createTimeEnd;
	@AutoDocProperty(value = "审核时间开始，格式：yyyy-MM-dd HH:mm:ss")
	private String examineTimeBegin;
	@AutoDocProperty(value = "审核时间结束，格式：yyyy-MM-dd HH:mm:ss")
	private String examineTimeEnd;
	@AutoDocProperty(value = "申请日期开始，格式：yyyy-MM-dd")
	private String applyDateBegin;
	@AutoDocProperty(value = "申请日期结束，格式：yyyy-MM-dd")
	private String applyDateEnd;
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getMemNo() {
		return memNo;
	}
	public void setMemNo(String memNo) {
		this.memNo = memNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getCreateTimeBegin() {
		return createTimeBegin;
	}
	public void setCreateTimeBegin(String createTimeBegin) {
		this.createTimeBegin = createTimeBegin;
	}
	public String getCreateTimeEnd() {
		return createTimeEnd;
	}
	public void setCreateTimeEnd(String createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}
	public String getExamineTimeBegin() {
		return examineTimeBegin;
	}
	public void setExamineTimeBegin(String examineTimeBegin) {
		this.examineTimeBegin = examineTimeBegin;
	}
	public String getExamineTimeEnd() {
		return examineTimeEnd;
	}
	public void setExamineTimeEnd(String examineTimeEnd) {
		this.examineTimeEnd = examineTimeEnd;
	}
	public String getApplyDateBegin() {
		return applyDateBegin;
	}
	public void setApplyDateBegin(String applyDateBegin) {
		this.applyDateBegin = applyDateBegin;
	}
	public String getApplyDateEnd() {
		return applyDateEnd;
	}
	public void setApplyDateEnd(String applyDateEnd) {
		this.applyDateEnd = applyDateEnd;
	}

}
