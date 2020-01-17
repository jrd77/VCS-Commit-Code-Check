package com.atzuche.order.coreapi.entity.vo;

import java.io.Serializable;

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
}
