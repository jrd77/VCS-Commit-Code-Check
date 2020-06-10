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
}
