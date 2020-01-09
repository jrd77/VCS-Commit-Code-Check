package com.atzuche.order.admin.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderInsuranceImportRequestDTO {

    @AutoDocProperty(value = "保险公司")
    private String insuranceCompany;

    @AutoDocProperty(value = "保单号")
    private String insuranceNo;

    @AutoDocProperty(value = "创建人")
    private String createOp;

    @AutoDocProperty(value = "oss文件路径")
    private String ossFileKey;


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

    public String getCreateOp() {
        return createOp;
    }

    public void setCreateOp(String createOp) {
        this.createOp = createOp;
    }

    public String getOssFileKey() {
        return ossFileKey;
    }

    public void setOssFileKey(String ossFileKey) {
        this.ossFileKey = ossFileKey;
    }

}
