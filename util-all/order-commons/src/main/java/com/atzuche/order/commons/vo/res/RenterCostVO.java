package com.atzuche.order.commons.vo.res;

import lombok.Data;

/**
 * 租客租车费用  押金  违章押金  信息
 */
@Data
public class RenterCostVO {
    /**
     * 订单号
     */
    private String orderNo;
    
    
    // ------------------------------------------
    /**
     * 应扣 租车费用 
     */
    private int renterCostFeeYingkou;
    /**
     * 实扣 租车费用
     */
    private int renterCostFeeShikou;
    
    
    /**
     * 应退 租车费用 
     */
    private int renterCostFeeYingtui;
    /**
     * 实退 租车费用
     */
    private int renterCostFeeShitui;

    
    /**
     * 应收租车费用
     */
    private int renterCostFeeYingshou;
    /**
     * 实收租车费用,补付实收
     */
    private int renterCostFeeShishou;
    
    // ------------------------------------------------ 补付
    /**
     * 补付租车费用实付
     */
    private int renterCostBufuShishou;
    /**
     * 补付租车费用应付
     */
    private int renterCostBufuYingshou;
 // ------------------------------------------------ 补付



    // ------------------------------ 租车押金 
    /**
     * 实扣押金
     */
    private int depositCostShikou;
    /**
     * 应扣押金
     */
    private int depositCostYingkou;
    
    /**
     * 实退押金
     */
    private int depositCostShitui;
    /**
     * 应退押金
     */
    private int depositCostYingtui;

    /**
     * 应付租车押金
     */
    private int depositCostYingshou;
    /**
     * 实付租车押金
     */
    private int depositCostShishou;
    
    /**
     * 实付租车押金(免押-预授权)
     */
    private int depositCostShishouAuth;
    // ------------------------------ 租车押金 
    
    
    // ------------------------------------------ 违章押金
    /**
     * 实扣违章押金
     */
    private int depositWzCostShikou;
    /**
     * 应扣违章押金
     */
    private int depositWzCostYingkou;
    
    /**
     * 实退违章押金
     */
    private int depositWzCostShitui;
    /**
     * 应退违章押金
     */
    private int depositWzCostYingtui;

    /**
     * 应付违章押金
     */
    private int depositWzCostYingshou;
    /**
     * 实付违章押金
     */
    private int depositWzCostShishou;
    /**
     * 实付违章押金(免押-预授权)
     */
    private int depositWzCostShishouAuth;
    
}
