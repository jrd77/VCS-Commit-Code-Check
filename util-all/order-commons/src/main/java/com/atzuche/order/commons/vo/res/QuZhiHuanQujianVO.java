package com.atzuche.order.commons.vo.res;

import com.autoyol.doc.annotation.AutoDocProperty;

public class QuZhiHuanQujianVO {

	@AutoDocProperty(value = "租客最早送达/车主最早取车时间：yyyy-MM-dd HH:mm")
	private String rentTimeStart;
	@AutoDocProperty(value = "租客最晚送达/车主最晚取车时间：yyyy-MM-dd HH:mm")
	private String rentTimeEnd;
	@AutoDocProperty(value = "租客建议送达时间/车主建议取车时间 yyyy-MM-dd HH:mm")
	private String defaultRentTime;
	@AutoDocProperty(value = "租客预计取车时间/车主预计送达时间 yyyy-MM-dd HH:mm")
	private String expectRevertTime;
	@AutoDocProperty(value = "取车提前时间（使用取车服务时），单位为分钟(车主)")
	private Integer getCarBeforeTime;
	@AutoDocProperty(value = "租客实际送达时间/车主实际取车时间 yyyy-MM-dd HH:mm")
	private String realRentTime;
	@AutoDocProperty(value = "租客实际取车时间/车主实际送达时间 yyyy-MM-dd HH:mm")
	private String realRevertTime;
	/**
	 * 会员身份类型：1-租客，2-车主
	 */
	private Integer memType;
	
	public Integer getMemType() {
		return memType;
	}
	public void setMemType(Integer memType) {
		this.memType = memType;
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
	public String getDefaultRentTime() {
		return defaultRentTime;
	}
	public void setDefaultRentTime(String defaultRentTime) {
		this.defaultRentTime = defaultRentTime;
	}
	public String getExpectRevertTime() {
		return expectRevertTime;
	}
	public void setExpectRevertTime(String expectRevertTime) {
		this.expectRevertTime = expectRevertTime;
	}
	public Integer getGetCarBeforeTime() {
		return getCarBeforeTime;
	}
	public void setGetCarBeforeTime(Integer getCarBeforeTime) {
		this.getCarBeforeTime = getCarBeforeTime;
	}
	public String getRealRentTime() {
		return realRentTime;
	}
	public void setRealRentTime(String realRentTime) {
		this.realRentTime = realRentTime;
	}
	public String getRealRevertTime() {
		return realRevertTime;
	}
	public void setRealRevertTime(String realRevertTime) {
		this.realRevertTime = realRevertTime;
	}
	
}
