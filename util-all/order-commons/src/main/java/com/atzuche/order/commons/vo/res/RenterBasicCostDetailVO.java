package com.atzuche.order.commons.vo.res;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 租客基础费用，即没有减免过的费用，系统自动计算的费用
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/7 6:07 下午
 **/
@Data
@ToString
public class RenterBasicCostDetailVO {
    @AutoDocProperty(value = "订单号")
    private String orderNo;
    @AutoDocProperty(value = "租客会员号")
    private String memNo;
    @AutoDocProperty(value = "租金总额")
    private Integer rentAmt;
    @AutoDocProperty(value = "平台保障费总额")
    private Integer insuranceAmt;
    @AutoDocProperty(value = "全面保障费总额")
    private Integer abatementInsuranceAmt;
    @AutoDocProperty(value = "手续费")
    private Integer fee;
    @AutoDocProperty(value = "取车服务费总额")
    private Integer srvGetCostAmt;
    @AutoDocProperty(value = "还车服务费总额")
    private Integer srvReturnCostAmt;
    @AutoDocProperty(value = "超里程费用总额")
    private Integer mileageCostAmt;
    @AutoDocProperty(value = "油费总额")
    private Integer oilCostAmt;
    @AutoDocProperty(value = "罚金总额")
    private Integer fineAmt;
    @AutoDocProperty(value = "附加驾驶人保险总额")
    private Integer extraDriverInsuranceAmt;
    @AutoDocProperty(value = "修改取还车罚金总额")
    private Integer getReturnFineAmt;
    @AutoDocProperty(value = "取车高峰运能费总额")
    private Integer getBlockedRaiseAmt;
    @AutoDocProperty(value = "还车高峰运能费总额")
    private Integer returnBlockedRaiseAmt;
    @AutoDocProperty(value = "取消违约金总额")
    private Integer renterPenaltyAmt;
}
