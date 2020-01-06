package com.atzuche.order.admin.dto;
import java.util.List;

/**
 * Created by dongdong.zhao on 2019/1/29.
 */
public class InsurancePurchaseResultDTO {


    private List<InsurancePurchaseDTO> insuranceList;

    public List<InsurancePurchaseDTO> getInsuranceList() {
        return insuranceList;
    }

    public void setInsuranceList(List<InsurancePurchaseDTO> insuranceList) {
        this.insuranceList = insuranceList;
    }

}
