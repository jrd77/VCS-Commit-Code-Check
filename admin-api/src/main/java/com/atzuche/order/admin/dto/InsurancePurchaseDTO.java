package com.atzuche.order.admin.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class InsurancePurchaseDTO {


    @AutoDocProperty(value="订单号")
    private String orderNo;
    @AutoDocProperty(value="保险公司")
    private String insuranceCompany;
    @AutoDocProperty(value="保单号")
    private String insuranceNo;
    @AutoDocProperty(value="保单费")
    private String insuranceFee;
    @AutoDocProperty(value="保单生成日期")
    private String insuranceDate;
    @AutoDocProperty(value="保单起期")
    private String insuranceStart;
    @AutoDocProperty(value="保单止期")
    private String insuranceEnd;

    /**
     * 1 手工 2 导入 3BI
     */
    private int type;


}
