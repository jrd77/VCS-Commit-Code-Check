package com.atzuche.order.admin.dto;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class InsurancePurchaseResultDTO {

    //保险列表
    private List<InsurancePurchaseDTO> insuranceList;

    public List<InsurancePurchaseDTO> getInsuranceList() {
        return insuranceList;
    }

    public void setInsuranceList(List<InsurancePurchaseDTO> insuranceList) {
        this.insuranceList = insuranceList;
    }

}
