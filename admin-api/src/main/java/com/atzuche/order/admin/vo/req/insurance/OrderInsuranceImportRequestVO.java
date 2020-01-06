package com.atzuche.order.admin.vo.req.insurance;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * Created by qincai.lin on 2019/12/30.
 */
public class OrderInsuranceImportRequestVO {

    @AutoDocProperty(value = "保险公司")
    private String insuranceCompany;

    @AutoDocProperty(value = "保单号")
    private String insuranceNo;

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getInsuranceNo() {
        return insuranceNo;
    }

    public void setInsuranceNo(String insuranceNo) {
        this.insuranceNo = insuranceNo;
    }

}
