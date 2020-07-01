package com.atzuche.order.commons.vo.res;

import java.io.Serializable;

import com.autoyol.doc.annotation.AutoDocProperty;

public class SectionDeliveryVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5695359061272267335L;
	
	@AutoDocProperty(value = "配送模式：0-区间配送，1-精准配送")
    private Integer distributionMode;
	@AutoDocProperty(value = "租客最早送达/车主最早取车时间：yyyy-MM-dd HH:mm:ss")
	private String rentTimeStart;
	@AutoDocProperty(value = "租客最晚送达/车主最晚取车时间：yyyy-MM-dd HH:mm:ss")
	private String rentTimeEnd;
	@AutoDocProperty(value = "租客最早取车/车主最早送达时间：yyyy-MM-dd HH:mm:ss")
	private String revertTimeStart;
	@AutoDocProperty(value = "租客最晚取车/车主最晚送达时间：yyyy-MM-dd HH:mm:ss")
	private String revertTimeEnd;
	// 租客默认建议送达时间/车主默认建议取车时间 yyyy-MM-dd HH:mm:ss
	private String defaultRentTime;
	// 租客默认建议取车时间/车主默认建议送达时间 yyyy-MM-dd HH:mm:ss
	private String defaultRevertTime;
	@AutoDocProperty(value = "取车精准达费用")
	private Integer accurateGetSrvUnit;
	@AutoDocProperty(value = "还车精准达费用")
    private Integer accurateReturnSrvUnit;
	
	public Integer getDistributionMode() {
		return distributionMode;
	}
	public void setDistributionMode(Integer distributionMode) {
		this.distributionMode = distributionMode;
	}
	public String getRentTimeStart() {
		return rentTimeStart;
	}
	public void setRentTimeStart(String rentTimeStart) {
		this.rentTimeStart = rentTimeStart;
	}
	public String getRentTimeEnd() {
		return rentTimeEnd;
	}
	public void setRentTimeEnd(String rentTimeEnd) {
		this.rentTimeEnd = rentTimeEnd;
	}
	public String getRevertTimeStart() {
		return revertTimeStart;
	}
	public void setRevertTimeStart(String revertTimeStart) {
		this.revertTimeStart = revertTimeStart;
	}
	public String getRevertTimeEnd() {
		return revertTimeEnd;
	}
	public void setRevertTimeEnd(String revertTimeEnd) {
		this.revertTimeEnd = revertTimeEnd;
	}
	public String getDefaultRentTime() {
		return defaultRentTime;
	}
	public void setDefaultRentTime(String defaultRentTime) {
		this.defaultRentTime = defaultRentTime;
	}
	public String getDefaultRevertTime() {
		return defaultRevertTime;
	}
	public void setDefaultRevertTime(String defaultRevertTime) {
		this.defaultRevertTime = defaultRevertTime;
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
