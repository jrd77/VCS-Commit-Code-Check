package com.atzuche.order.admin.vo.resp.car;

import com.autoyol.doc.annotation.AutoDocProperty;

public class CarDepositOtherRespVO implements java.io.Serializable {
    @AutoDocProperty("预计抵扣租车费用金额")
    private Integer reductiAmt;
    @AutoDocProperty("剩余可用车辆押金")
    private Integer surplusAmt;

    @AutoDocProperty("风控-暂扣车辆押金")
    private Integer managementAmt;
    @AutoDocProperty("风控-暂扣原因")
    private String managementRemark;

    @AutoDocProperty("交易-暂扣车辆押金")
    private Integer transactionAmt;
    @AutoDocProperty("交易-暂扣原因")
    private String transactionRemark;

    @AutoDocProperty("理赔-暂扣车辆押金")
    private Integer claimAmt;
    @AutoDocProperty("理赔-暂扣原因")
    private String claimRemark;

    @AutoDocProperty("车辆押金预计结算时间")
    private String estimateDateStr;
    @AutoDocProperty("车辆押金实际结算时间")
    private String realityDateStr;

    @AutoDocProperty("实际已暂扣金额")
    private Integer money;
    @AutoDocProperty("扣款状态（最新暂扣信息）")
    private String statusStr;

    @AutoDocProperty("扣款时间（最新暂扣信息）")
    private String dateStr;
}
