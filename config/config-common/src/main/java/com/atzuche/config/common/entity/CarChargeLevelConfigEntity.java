package com.atzuche.config.common.entity;

import java.io.Serializable;

public class CarChargeLevelConfigEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8817611419942081102L;
	
	private Integer id;
	private Integer level;
	private Integer agreementStopFreightRate;
	private Integer notagreementStopFreightRate;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getAgreementStopFreightRate() {
		return agreementStopFreightRate;
	}
	public void setAgreementStopFreightRate(Integer agreementStopFreightRate) {
		this.agreementStopFreightRate = agreementStopFreightRate;
	}
	public Integer getNotagreementStopFreightRate() {
		return notagreementStopFreightRate;
	}
	public void setNotagreementStopFreightRate(Integer notagreementStopFreightRate) {
		this.notagreementStopFreightRate = notagreementStopFreightRate;
	}

}
