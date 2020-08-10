package com.atzuche.order.commons.vo;

import java.util.List;

public class RenterInsureCoefficientVO {

	/**
	 * 主订单号
	 */
	private String orderNo;

	/**
	 * 租客子订单号
	 */
    private String renterOrderNo;

    /**
     * 系数
     */
    private Double coefficient;

    /**
     * 类型：1-驾龄系数，2-易出险车系数，3-驾驶行为系数，4-折扣
     */
    private Integer type; 
    
    /**
     * 系数计算因数列表
     */
    private List<RenterInsureCoefficientReasonVO> reasonList;
    
    public RenterInsureCoefficientVO() {}

	public RenterInsureCoefficientVO(String orderNo, String renterOrderNo, Double coefficient, Integer type) {
		this.orderNo = orderNo;
		this.renterOrderNo = renterOrderNo;
		this.coefficient = coefficient;
		this.type = type;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getRenterOrderNo() {
		return renterOrderNo;
	}

	public void setRenterOrderNo(String renterOrderNo) {
		this.renterOrderNo = renterOrderNo;
	}

	public Double getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(Double coefficient) {
		this.coefficient = coefficient;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public List<RenterInsureCoefficientReasonVO> getReasonList() {
		return reasonList;
	}

	public void setReasonList(List<RenterInsureCoefficientReasonVO> reasonList) {
		this.reasonList = reasonList;
	}
    
}
