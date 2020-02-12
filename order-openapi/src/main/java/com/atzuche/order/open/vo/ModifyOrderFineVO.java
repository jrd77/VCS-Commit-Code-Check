package com.atzuche.order.open.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ModifyOrderFineVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6314260392760375183L;

	/**
     * 提前还车违约金
     */
    private Integer penaltyAmt;
    
    /**
     * 取车服务违约金
     */
    private Integer getFineAmt;
    
    /**
     * 还车服务违约金
     */
    private Integer returnFineAmt;
}
