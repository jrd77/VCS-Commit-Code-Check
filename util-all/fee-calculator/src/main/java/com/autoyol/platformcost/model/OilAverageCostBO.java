package com.autoyol.platformcost.model;


import java.io.Serializable;
public class OilAverageCostBO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6219796780600805733L;
	/**
	 * 城市编号
	 */
	private Integer cityCode;
	/**
	 * 动力类型，1：92号汽油、2：95号汽油、3：0号柴油、4：纯电动、5: 98号汽油
	 */
	private Integer engineType;
	/**
	 * 分子
	 */
	private Integer  molecule;
	/**
	 * 分母
	 */
	private Integer denominator;
	
	public Integer getCityCode() {
		return cityCode;
	}
	public void setCityCode(Integer cityCode) {
		this.cityCode = cityCode;
	}
	public Integer getEngineType() {
		return engineType;
	}
	public void setEngineType(Integer engineType) {
		this.engineType = engineType;
	}
	public Integer getMolecule() {
		return molecule;
	}
	public void setMolecule(Integer molecule) {
		this.molecule = molecule;
	}
	public Integer getDenominator() {
		return denominator;
	}
	public void setDenominator(Integer denominator) {
		this.denominator = denominator;
	}
}