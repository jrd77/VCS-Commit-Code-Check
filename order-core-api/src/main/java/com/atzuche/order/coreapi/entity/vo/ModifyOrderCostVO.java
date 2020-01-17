package com.atzuche.order.coreapi.entity.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ModifyOrderCostVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8288443049640478932L;

	/**
     * 车辆租金
     */
    private Integer rentAmt;
    
    /**
     * 手续费
     */
    private Integer poundageAmt;
    
    /**
     * 基础保障费
     */
    private Integer insuranceAmt;

    /**
     * 超极补充全险
     */
    private Integer abatementAmt;

    /**
     * 附加驾驶员险
     */
    private Integer totalDriverFee;

    /**
     * 取车费用
     */
    private Integer getCost;

    /**
     * 还车费用
     */
    private Integer returnCost;
    
    /**
     * 取车运能加价（取车受阻继续下单加价）
     */
    private Integer getBlockedRaiseAmt;
    
    /**
     * 还车运能加价（还车受阻继续下单加价）
     */
    private Integer returnBlockedRaiseAmt;
}
