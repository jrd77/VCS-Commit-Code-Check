package com.atzuche.order.commons.vo;

import com.atzuche.order.commons.vo.res.SummarySectionDeliveryVO;
import com.autoyol.doc.annotation.AutoDocProperty;

public class RenterOwnerSummarySectionDeliveryVO {
	@AutoDocProperty(value = "租客区间配送信息")
	private SummarySectionDeliveryVO renter;
	@AutoDocProperty(value = "车主区间配送信息")
	private SummarySectionDeliveryVO owner;
	@AutoDocProperty(value = "配送模式：0-区间配送，1-精准配送")
	private Integer distributionMode; 
	@AutoDocProperty(value = "精准取车服务费单价")
	private Integer accurateGetSrvUnit;
	@AutoDocProperty(value = "精准还车服务费单价")
	private Integer accurateReturnSrvUnit;
	public SummarySectionDeliveryVO getRenter() {
		return renter;
	}
	public void setRenter(SummarySectionDeliveryVO renter) {
		this.renter = renter;
	}
	public SummarySectionDeliveryVO getOwner() {
		return owner;
	}
	public void setOwner(SummarySectionDeliveryVO owner) {
		this.owner = owner;
	}
	public Integer getDistributionMode() {
		return distributionMode;
	}
	public void setDistributionMode(Integer distributionMode) {
		this.distributionMode = distributionMode;
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
	
}
