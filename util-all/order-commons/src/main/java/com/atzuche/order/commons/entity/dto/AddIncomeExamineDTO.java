package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddIncomeExamineDTO {
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
	@AutoDocProperty("当前页码")
    private String pageNumber;
	@AutoDocProperty("一页大小")
    private String pageSize;
}
