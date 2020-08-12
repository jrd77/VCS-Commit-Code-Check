package com.atzuche.order.commons.vo.res;

public class SectionDeliveryResultVO {

	/**
	 * 租客处区间配送信息
	 */
	private SectionDeliveryVO renterSectionDelivery;
	/**
	 * 车主处区间配送信息
	 */
	private SectionDeliveryVO ownerSectionDelivery;
	/**
	 * 配送模式：0-区间配送，1-精准配送
	 */
	private Integer distributionMode;
	
	public SectionDeliveryVO getRenterSectionDelivery() {
		return renterSectionDelivery;
	}
	public void setRenterSectionDelivery(SectionDeliveryVO renterSectionDelivery) {
		this.renterSectionDelivery = renterSectionDelivery;
	}
	public SectionDeliveryVO getOwnerSectionDelivery() {
		return ownerSectionDelivery;
	}
	public void setOwnerSectionDelivery(SectionDeliveryVO ownerSectionDelivery) {
		this.ownerSectionDelivery = ownerSectionDelivery;
	}
	public Integer getDistributionMode() {
		return distributionMode;
	}
	public void setDistributionMode(Integer distributionMode) {
		this.distributionMode = distributionMode;
	}
	
}
