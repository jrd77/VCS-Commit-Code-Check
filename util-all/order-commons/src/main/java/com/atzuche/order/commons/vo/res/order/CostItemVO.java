package com.atzuche.order.commons.vo.res.order;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * 费用信息
 *
 * @author pengcheng.fu
 * @date 2020/1/11 15:16
 */
public class CostItemVO {

    @AutoDocProperty(value = "费用项编码,如:11110003(车辆租金) 等")
    private String costCode;

    @AutoDocProperty(value = "费用项描述,如:车辆租金 等")
    private String costDesc;

    @AutoDocProperty(value = "费用项单价")
    private Integer unitPrice;

    @AutoDocProperty(value = "费用项份数")
    private Double count;

    @AutoDocProperty(value = "费用项总金额")
    private Integer totalAmount;

    public String getCostCode() {
        return costCode;
    }

    public void setCostCode(String costCode) {
        this.costCode = costCode;
    }

    public String getCostDesc() {
        return costDesc;
    }

    public void setCostDesc(String costDesc) {
        this.costDesc = costDesc;
    }

    public Integer getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }
}
