package com.atzuche.order.accountownerincome.entity;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.util.Date;

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

    private Integer origDebt;

    private Integer nowDebt;
    @AutoDocProperty(value = "审核状态-0未审核 1审核通过 2审核不通过 3 审核中，待核查（异常）4审核中，待核查（测试）5其他")
    private Integer status;

    private String remark;
    
    private Long addId;
    @AutoDocProperty(value = "创建时间")
    private Date createTime;

    private String createOp;

    private Date updateTime;

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

    public Long getAddId() {
		return addId;
	}

	public void setAddId(Long addId) {
		this.addId = addId;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
    
}