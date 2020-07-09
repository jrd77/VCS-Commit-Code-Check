package com.atzuche.order.commons.vo.res;

import com.autoyol.doc.annotation.AutoDocProperty;

public class QuHuanQujianVO {

	@AutoDocProperty(value = "租客最早送达/车主最早取车时间：yyyy-MM-dd HH:mm")
	private String rentTimeStart;
	@AutoDocProperty(value = "租客最晚送达/车主最晚取车时间：yyyy-MM-dd HH:mm")
	private String rentTimeEnd;
	@AutoDocProperty(value = "租客最早取车/车主最早送达时间：yyyy-MM-dd HH:mm")
	private String revertTimeStart;
	@AutoDocProperty(value = "租客最晚取车/车主最晚送达时间：yyyy-MM-dd HH:mm")
	private String revertTimeEnd;
	@AutoDocProperty(value = "租客建议送达时间/车主建议取车时间 yyyy-MM-dd HH:mm")
	private String defaultRentTime;
	@AutoDocProperty(value = "租客建议取车时间/车主建议送达时间 yyyy-MM-dd HH:mm")
	private String defaultRevertTime;
	@AutoDocProperty(value = "取车提前时间（使用取车服务时），单位为分钟(车主)")
	private Integer getCarBeforeTime;
	@AutoDocProperty(value = "还车延后时间（使用还车服务时），单位为分钟(车主)")
	private Integer returnCarAfterTime;
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
	public Integer getGetCarBeforeTime() {
		return getCarBeforeTime;
	}
	public void setGetCarBeforeTime(Integer getCarBeforeTime) {
		this.getCarBeforeTime = getCarBeforeTime;
	}
	public Integer getReturnCarAfterTime() {
		return returnCarAfterTime;
	}
	public void setReturnCarAfterTime(Integer returnCarAfterTime) {
		this.returnCarAfterTime = returnCarAfterTime;
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
