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
public class RenterCostDetailVO {
    @AutoDocProperty(value = "订单号")
    private String orderNo;
    @AutoDocProperty(value = "子单号")
    private String renterOrderNo;
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
    @AutoDocProperty(value = "附加驾驶人保险总额")
    private Integer extraDriverInsuranceAmt;

    @AutoDocProperty(value = "配送费详情及其总额")
    private RenterDeliveryFeeDetailVO deliveryFeeDetail;

    @AutoDocProperty(value = "超里程费用总额")
    private Integer mileageCostAmt;
    @AutoDocProperty(value = "油费总额")
    private Integer oilCostAmt;
    @AutoDocProperty(value = "罚金及其详情")
    private RenterFineVO fineDetail;

    @AutoDocProperty(value = "补贴及其详情")
    private RenterSubsidyDetailVO subsidyDetail;







}


