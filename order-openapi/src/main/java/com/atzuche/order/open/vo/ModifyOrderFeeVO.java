package com.atzuche.order.open.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ModifyOrderFeeVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2349049671930791788L;

	/**
	 * 费用
	 */
	private ModifyOrderCostVO modifyOrderCostVO;
	
	/**
	 * 抵扣
	 */
	private ModifyOrderDeductVO modifyOrderDeductVO;
	
	/**
	 * 罚金
	 */
	private ModifyOrderFineVO modifyOrderFineVO;
}
