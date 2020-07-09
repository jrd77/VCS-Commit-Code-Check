package com.atzuche.order.delivery.entity;

import java.util.Date;

public class RenterOrderDeliveryMode {
    private Integer id;

    private String orderNo;

    private String renterOrderNo;

    private Integer distributionMode;

    private Integer rentBeforeMinutes;

    private Integer rentAfterMinutes;

    private Integer revertBeforeMinutes;

    private Integer revertAfterMinutes;

    private Integer accurateGetSrvUnit;

    private Integer accurateReturnSrvUnit;
    
    private Date renterProposalGetTime;

    private Date renterProposalReturnTime;

    private Date ownerProposalGetTime;

    private Date ownerProposalReturnTime;

    private Date createTime;

    private String createOp;

    private Date updateTime;

    private String updateOp;

    private Integer isDelete;

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

    public String getRenterOrderNo() {
        return renterOrderNo;
    }

    public void setRenterOrderNo(String renterOrderNo) {
        this.renterOrderNo = renterOrderNo == null ? null : renterOrderNo.trim();
    }

    public Integer getDistributionMode() {
        return distributionMode;
    }

    public void setDistributionMode(Integer distributionMode) {
        this.distributionMode = distributionMode;
    }

    public Integer getRentBeforeMinutes() {
        return rentBeforeMinutes;
    }

    public void setRentBeforeMinutes(Integer rentBeforeMinutes) {
        this.rentBeforeMinutes = rentBeforeMinutes;
    }

    public Integer getRentAfterMinutes() {
        return rentAfterMinutes;
    }

    public void setRentAfterMinutes(Integer rentAfterMinutes) {
        this.rentAfterMinutes = rentAfterMinutes;
    }

    public Integer getRevertBeforeMinutes() {
        return revertBeforeMinutes;
    }

    public void setRevertBeforeMinutes(Integer revertBeforeMinutes) {
        this.revertBeforeMinutes = revertBeforeMinutes;
    }

    public Integer getRevertAfterMinutes() {
        return revertAfterMinutes;
    }

    public void setRevertAfterMinutes(Integer revertAfterMinutes) {
        this.revertAfterMinutes = revertAfterMinutes;
    }

    public Integer getAccurateGetSrvUnit() {
        return accurateGetSrvUnit;
    }

    public void setAccurateGetSrvUnit(Integer accurateGetSrvUnit) {
        this.accurateGetSrvUnit = accurateGetSrvUnit;
    }

    public Integer getAccurateReturnSrvUnit() {
        return accurateReturnSrvUnit;
    }

    public void setAccurateReturnSrvUnit(Integer accurateReturnSrvUnit) {
        this.accurateReturnSrvUnit = accurateReturnSrvUnit;
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

	public Date getRenterProposalGetTime() {
		return renterProposalGetTime;
	}

	public void setRenterProposalGetTime(Date renterProposalGetTime) {
		this.renterProposalGetTime = renterProposalGetTime;
	}

	public Date getRenterProposalReturnTime() {
		return renterProposalReturnTime;
	}

	public void setRenterProposalReturnTime(Date renterProposalReturnTime) {
		this.renterProposalReturnTime = renterProposalReturnTime;
	}

	public Date getOwnerProposalGetTime() {
		return ownerProposalGetTime;
	}

	public void setOwnerProposalGetTime(Date ownerProposalGetTime) {
		this.ownerProposalGetTime = ownerProposalGetTime;
	}

	public Date getOwnerProposalReturnTime() {
		return ownerProposalReturnTime;
	}

	public void setOwnerProposalReturnTime(Date ownerProposalReturnTime) {
		this.ownerProposalReturnTime = ownerProposalReturnTime;
	}
    
}