package com.atzuche.order.commons.vo.res;

import com.autoyol.doc.annotation.AutoDocProperty;

public class ZhiquZhihuanVO {

	@AutoDocProperty(value = "租客预计送达时间/车主预计取车时间 yyyy-MM-dd HH:mm")
	private String expectRentTime;
	@AutoDocProperty(value = "租客预计取车时间/车主预计送达时间 yyyy-MM-dd HH:mm")
	private String expectRevertTime;
	@AutoDocProperty(value = "租客实际送达时间/车主实际取车时间 yyyy-MM-dd HH:mm")
	private String realRentTime;
	@AutoDocProperty(value = "租客实际取车时间/车主实际送达时间 yyyy-MM-dd HH:mm")
	private String realRevertTime;
    @AutoDocProperty(value = "取车提前时间，单位为分钟(车主)")
    private Integer getCarBeforeTime;
    @AutoDocProperty(value = "还车延后时间，单位为分钟(车主)")
    private Integer returnCarAfterTime;
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
	public String getExpectRentTime() {
		return expectRentTime;
	}
	public void setExpectRentTime(String expectRentTime) {
		this.expectRentTime = expectRentTime;
	}
	public String getExpectRevertTime() {
		return expectRevertTime;
	}
	public void setExpectRevertTime(String expectRevertTime) {
		this.expectRevertTime = expectRevertTime;
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
}
