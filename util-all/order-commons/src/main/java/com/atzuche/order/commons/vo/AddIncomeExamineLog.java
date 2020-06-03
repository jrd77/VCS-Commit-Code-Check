package com.atzuche.order.commons.vo;

import java.util.Date;

import com.atzuche.order.commons.DateUtils;
import com.autoyol.doc.annotation.AutoDocProperty;

public class AddIncomeExamineLog {
    private Long id;

    private Integer addIncomeExamineId;

    private Integer examineResultStatus;
    @AutoDocProperty(value = "审核结果")
    private String examineResultStatusTxt;
    @AutoDocProperty(value = "备注")
    private String remark;
    @AutoDocProperty(value = "审核人")
    private String examineOperator;

    private Date examineTime;
    @AutoDocProperty(value = "审核时间")
    private String examineTimeStr;
    private Date createTime;

    private String createOp;

    private Date updateTime;

    private String updateOp;

    private Integer isDelete;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAddIncomeExamineId() {
        return addIncomeExamineId;
    }

    public void setAddIncomeExamineId(Integer addIncomeExamineId) {
        this.addIncomeExamineId = addIncomeExamineId;
    }

    public Integer getExamineResultStatus() {
        return examineResultStatus;
    }

    public void setExamineResultStatus(Integer examineResultStatus) {
        this.examineResultStatus = examineResultStatus;
    }

    public String getExamineResultStatusTxt() {
        return examineResultStatusTxt;
    }

    public void setExamineResultStatusTxt(String examineResultStatusTxt) {
        this.examineResultStatusTxt = examineResultStatusTxt == null ? null : examineResultStatusTxt.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getExamineOperator() {
        return examineOperator;
    }

    public void setExamineOperator(String examineOperator) {
        this.examineOperator = examineOperator == null ? null : examineOperator.trim();
    }

    public Date getExamineTime() {
        return examineTime;
    }

    public void setExamineTime(Date examineTime) {
        this.examineTime = examineTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateOp() {
        return createOp;
    }

    public void setCreateOp(String createOp) {
        this.createOp = createOp == null ? null : createOp.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateOp() {
        return updateOp;
    }

    public void setUpdateOp(String updateOp) {
        this.updateOp = updateOp == null ? null : updateOp.trim();
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

	public String getExamineTimeStr() {
		if (examineTime != null) {
			examineTimeStr = DateUtils.formate(examineTime, DateUtils.DATE_DEFAUTE1);
		}
		return examineTimeStr;
	}

	public void setExamineTimeStr(String examineTimeStr) {
		this.examineTimeStr = examineTimeStr;
	}
    
}