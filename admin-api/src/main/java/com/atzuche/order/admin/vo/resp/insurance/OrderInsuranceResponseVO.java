package com.atzuche.order.admin.vo.resp.insurance;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.util.List;

/**
 * Created by qincai.lin on 2019/12/30.
 */
public class OrderInsuranceResponseVO {

    @AutoDocProperty(value = "预计购买保险信息")
    private List<OrderInsuranceVO> exceptedOrderInsuranceList;

    @AutoDocProperty(value = "实际购买保险信息")
    private List<OrderInsuranceVO> realOrderInsuranceList;

    public List<OrderInsuranceVO> getExceptedOrderInsuranceList() {
        return exceptedOrderInsuranceList;
    }

    public void setExceptedOrderInsuranceList(List<OrderInsuranceVO> exceptedOrderInsuranceList) {
        this.exceptedOrderInsuranceList = exceptedOrderInsuranceList;
    }

    public List<OrderInsuranceVO> getRealOrderInsuranceList() {
        return realOrderInsuranceList;
    }

    public void setRealOrderInsuranceList(List<OrderInsuranceVO> realOrderInsuranceList) {
        this.realOrderInsuranceList = realOrderInsuranceList;
    }

}
