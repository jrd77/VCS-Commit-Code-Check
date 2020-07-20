package com.atzuche.order.commons.vo.res;

import com.autoyol.doc.annotation.AutoDocProperty;

public class SummarySectionDeliveryVO {

	@AutoDocProperty(value = "取还车区间配送信息")
	private QuHuanQujianVO quHuanQujianVO;
	@AutoDocProperty(value = "取还车准时达")
	private QuhuanZhunshiVO quhuanZhunshiVO;
	@AutoDocProperty(value = "取车服务-自还-区间配送")
	private QuZhiHuanQujianVO quZhiHuanQujianVO;
	@AutoDocProperty(value = " 取车服务-自还-准时达")
	private QuZhiHuanZhunshiVO quZhiHuanZhunshiVO;
	@AutoDocProperty(value = "自取自还")
	private ZhiquZhihuanVO zhiquZhihuanVO;
	public QuHuanQujianVO getQuHuanQujianVO() {
		return quHuanQujianVO;
	}
	public void setQuHuanQujianVO(QuHuanQujianVO quHuanQujianVO) {
		this.quHuanQujianVO = quHuanQujianVO;
	}
	public QuhuanZhunshiVO getQuhuanZhunshiVO() {
		return quhuanZhunshiVO;
	}
	public void setQuhuanZhunshiVO(QuhuanZhunshiVO quhuanZhunshiVO) {
		this.quhuanZhunshiVO = quhuanZhunshiVO;
	}
	public QuZhiHuanQujianVO getQuZhiHuanQujianVO() {
		return quZhiHuanQujianVO;
	}
	public void setQuZhiHuanQujianVO(QuZhiHuanQujianVO quZhiHuanQujianVO) {
		this.quZhiHuanQujianVO = quZhiHuanQujianVO;
	}
	public QuZhiHuanZhunshiVO getQuZhiHuanZhunshiVO() {
		return quZhiHuanZhunshiVO;
	}
	public void setQuZhiHuanZhunshiVO(QuZhiHuanZhunshiVO quZhiHuanZhunshiVO) {
		this.quZhiHuanZhunshiVO = quZhiHuanZhunshiVO;
	}
	public ZhiquZhihuanVO getZhiquZhihuanVO() {
		return zhiquZhihuanVO;
	}
	public void setZhiquZhihuanVO(ZhiquZhihuanVO zhiquZhihuanVO) {
		this.zhiquZhihuanVO = zhiquZhihuanVO;
	}
	
}
