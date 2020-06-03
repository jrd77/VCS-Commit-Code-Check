package com.atzuche.order.commons.vo;

import java.util.Date;

import com.atzuche.order.commons.DateUtils;
import com.autoyol.doc.annotation.AutoDocProperty;

public class AddIncomeExamine {
	@AutoDocProperty(value = "收益审核记录id")
    private Integer id;
    @AutoDocProperty(value = "订单号")
    private String orderNo;
    @AutoDocProperty(value = "会员号")
    private Integer memNo;
    @AutoDocProperty(value = "手机号")
    private Long mobile;
    @AutoDocProperty(value = "会员姓名")
    private String name;
    private Integer memType;
    @AutoDocProperty(value = "会员身份")
    private String memTypeStr;
    @AutoDocProperty(value = "追加类型")
    private String type;
    @AutoDocProperty(value = "明细类型")
    private String detailType;
    @AutoDocProperty(value = "追加金额")
    private Integer amt;
    @AutoDocProperty(value = "追加说明")
    private String describe;
    @AutoDocProperty(value = "申请部门")
    private String department;
   
    private Date applyTime;
    @AutoDocProperty(value = "申请日志")
    private String applyTimeStr;
    private Integer origDebt;

    private Integer nowDebt;
    
    private Integer status;
    @AutoDocProperty(value = "审核状态")
    private String statusStr;
    @AutoDocProperty(value = "备注")
    private String remark;
    
    private Date createTime;
    @AutoDocProperty(value = "创建时间")
    private String createTimeStr;

    private String createOp;

    private Date updateTime;
    @AutoDocProperty(value = "审核时间")
    private String examineTime;

    private String updateOp;

    private Integer isDelete;
    
    @AutoDocProperty(value = "内部分类 1:普通,2:套餐,3:长租")
    private String category;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Integer getMemNo() {
        return memNo;
    }

    public void setMemNo(Integer memNo) {
        this.memNo = memNo;
    }

    public Long getMobile() {
        return mobile;
    }

    public void setMobile(Long mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getMemType() {
        return memType;
    }

    public void setMemType(Integer memType) {
        this.memType = memType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType == null ? null : detailType.trim();
    }

    public Integer getAmt() {
        return amt;
    }

    public void setAmt(Integer amt) {
        this.amt = amt;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe == null ? null : describe.trim();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department == null ? null : department.trim();
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Integer getOrigDebt() {
        return origDebt;
    }

    public void setOrigDebt(Integer origDebt) {
        this.origDebt = origDebt;
    }

    public Integer getNowDebt() {
        return nowDebt;
    }

    public void setNowDebt(Integer nowDebt) {
        this.nowDebt = nowDebt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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

	public String getMemTypeStr() {
		if (memType != null) {
			if (memType.intValue() == 0) {
				memTypeStr = "租客";
			} else {
				memTypeStr = "车主";
			}
		}
		return memTypeStr;
	}

	public void setMemTypeStr(String memTypeStr) {
		this.memTypeStr = memTypeStr;
	}

	public String getApplyTimeStr() {
		if (applyTime != null) {
			applyTimeStr = DateUtils.formate(applyTime, DateUtils.fmt_yyyyMMdd);
		}
		return applyTimeStr;
	}

	public void setApplyTimeStr(String applyTimeStr) {
		this.applyTimeStr = applyTimeStr;
	}
	
	public String getStatusStr() {
		if (status != null) {
			if (status.intValue() == 0) {
				statusStr = "未审核";
			} else if (status.intValue() == 1) {
				statusStr = "审核通过";
			} else if (status.intValue() == 2) {
				statusStr = "审核不通过";
			} else if (status.intValue() == 3) {
				statusStr = "审核中，待核查（异常）";
			} else if (status.intValue() == 4) {
				statusStr = "审核中，待核查（测试）";
			} else if (status.intValue() == 5) {
				statusStr = "其他";
			}
		}
		return statusStr;
	}

	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}

	public String getCreateTimeStr() {
		if (createTime != null) {
			createTimeStr = DateUtils.formate(createTime, DateUtils.DATE_DEFAUTE1);
		}
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public String getExamineTime() {
		if (updateTime != null) {
			examineTime = DateUtils.formate(updateTime, DateUtils.DATE_DEFAUTE1);
		}
		return examineTime;
	}

	public void setExamineTime(String examineTime) {
		this.examineTime = examineTime;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
    
}