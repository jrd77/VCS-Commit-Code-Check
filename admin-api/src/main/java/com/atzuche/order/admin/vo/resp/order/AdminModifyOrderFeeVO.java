package com.atzuche.order.admin.vo.resp.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/17 4:47 下午
 **/
@Data
@ToString
public class AdminModifyOrderFeeVO {
    /**
     * 车辆租金
     */
    @AutoDocProperty(value = "车辆租金")
    private Integer rentAmt;

    /**
     * 手续费
     */
    @AutoDocProperty(value = "手续费")
    private Integer poundageAmt;

    /**
     * 基础保障费
     */
    @AutoDocProperty(value = "基础保障费")
    private Integer insuranceAmt;

    /**
     * 超极补充全险
     */
    @AutoDocProperty(value = "超极补充全险")
    private Integer abatementAmt;

    /**
     * 附加驾驶员险
     */
    @AutoDocProperty(value = "附加驾驶员险")
    private Integer totalDriverFee;

    /**
     * 取车费用
     */
    @AutoDocProperty(value = "取车费用")
    private Integer getCost;

    /**
     * 还车费用
     */
    @AutoDocProperty(value = "还车费用")
    private Integer returnCost;

    /**
     * 取车运能加价（取车受阻继续下单加价）
     */
    @AutoDocProperty(value = "取车运能加价（取车受阻继续下单加价）")
    private Integer getBlockedRaiseAmt;

    /**
     * 还车运能加价（还车受阻继续下单加价）
     */
    @AutoDocProperty(value = "还车运能加价（还车受阻继续下单加价）")
    private Integer returnBlockedRaiseAmt;

    /**
     * 车主券抵扣金额
     */
    @AutoDocProperty(value = "车主券抵扣金额")
    private Integer ownerCouponOffsetCost;

    /**
     * 限时立减
     */
    @AutoDocProperty(value = "限时立减")
    private Integer reductionAmt;

    /**
     * 平台券抵扣金额
     */
    @AutoDocProperty(value = "平台券抵扣金额")
    private Integer discouponAmt;

    /**
     * 取送车优惠券抵扣金额
     */
    @AutoDocProperty(value = "取送车优惠券抵扣金额")
    private Integer getCarFeeDiscouponOffsetAmt;

    /**
     * 凹凸币抵扣金额
     */
    @AutoDocProperty(value = "凹凸币抵扣金额")
    private Integer autoCoinDeductibleAmt;


    /**
     * 提前还车违约金
     */
    @AutoDocProperty(value = "提前还车违约金")
    private Integer penaltyAmt;

    /**
     * 取车服务违约金
     */
    @AutoDocProperty(value = "取车服务违约金")
    private Integer getFineAmt;

    /**
     * 还车服务违约金
     */
    @AutoDocProperty(value = "还车服务违约金")
    private Integer returnFineAmt;
}
