package com.atzuche.order.commons.vo.rentercost;


import com.autoyol.doc.annotation.AutoDocProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@JsonInclude(Include.NON_EMPTY)
@Data
public class GetReturnResponseVO {
    /**
     * 取车费用
     */
    private int getFee;
    /**
     * 预期应收取车费用
     */
    private int getShouldFee;
    /**
     * 还车费用
     */
    private int returnFee;
    /**
     * 预期应收还车费用
     */
    private int returnShouldFee;
    /**
     * 取车城市送取车费用
     */
    private int getInitFee;
    /**
     * 还车城市送取车费用
     */
    private int returnInitFee;
    /**
     * 取车高峰溢价系数
     */
    private double getOverRate;
    /**
     * 还车高峰溢价系数
     */
    private double returnOverRate;


    /**
     * 取车高峰溢价系数
     */
    private Double getShowDistance;
    /**
     * 还车展示距离
     */
    private Double returnShowDistance;


    /**
     * 取车距离上浮系数
     */
    private Double getDistanceCoefficient;
    /**
     * 还车距离上浮系数
     */
    private Double returnDistanceCoefficient;

    //时间段溢价
    @AutoDocProperty("时间段溢价")
    private String getTimePeriodUpPrice;

    //圈层溢价
    @AutoDocProperty("圈层溢价")
    private String getCicrleUpPrice;

    //远途溢价
    @AutoDocProperty("远途溢价")
    private String getDistanceUpPrice;

    //时间段溢价
    @AutoDocProperty("时间段溢价")
    private String returnTimePeriodUpPrice;

    //圈层溢价
    @AutoDocProperty("圈层溢价")
    private String returnCicrleUpPrice;

    //远途溢价
    @AutoDocProperty("远途溢价")
    private String returnDistanceUpPrice;

    //租金+基础保险+全面保障+手续费
    private int sumJudgeFreeFee;


    public GetReturnResponseVO(int getFee, int getShouldFee, int returnFee, int returnShouldFee, int getInitFee, int returnInitFee, double getOverRate, double returnOverRate) {
        this.getFee = getFee;
        this.getShouldFee = getShouldFee;
        this.returnFee = returnFee;
        this.returnShouldFee = returnShouldFee;
        this.getInitFee = getInitFee;
        this.returnInitFee = returnInitFee;
        this.getOverRate = getOverRate;
        this.returnOverRate = returnOverRate;
    }
    public GetReturnResponseVO(){
        super();
    }



}
