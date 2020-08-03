package com.atzuche.order.open.vo;

import java.io.Serializable;

import com.atzuche.order.commons.entity.dto.RenterInsureCoefficientDTO;
import lombok.Data;
import lombok.ToString;
@Data
@ToString
public class ModifyOrderCompareVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8010848179823475689L;

	/**
	 * 修改前费用
	 */
	private ModifyOrderFeeVO initModifyOrderFeeVO;
	
	/**
	 * 修改后费用
	 */
	private ModifyOrderFeeVO updateModifyOrderFeeVO;
	
	/**
	 * 补付金额是否清0标志:1-清0，0-不清
	 */
	private Integer cleanSupplementAmt; 
	/**
	 * 实付金额
	 */
	private Integer rentAmtPayed;
	
	/**
	 * 需要补付金额
	 */
	private Integer needSupplementAmt;
    /*
    *
    * 驾驶行为评分和各项系数
    * */
	private RenterInsureCoefficientDTO renterInsureCoefficientDTO;
}
