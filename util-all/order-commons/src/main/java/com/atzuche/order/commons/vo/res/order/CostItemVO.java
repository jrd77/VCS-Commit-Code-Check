package com.atzuche.order.commons.vo.res.order;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * 费用信息
 *
 * @author pengcheng.fu
 * @date 2020/1/11 15:16
 */
public class CostItemVO {

    @AutoDocProperty(value = "费用项编码,如:11110003(租金),11110004(平台保障费),11110005(全面保障费),11110006(平台手续费),11110007(取车费用)," +
            "11110008(还车费用),11110036(取车运能加价),11110037(还车运能加价),11110027(附加驾驶保险金额) 等")
    private String costCode;

    @AutoDocProperty(value = "费用项描述,如:车辆租金,基础保障费,全面保障服务,手续费,上门送取服务费,附加驾驶员保障费, 等")
    private String costDesc;

    @AutoDocProperty(value = "费用项单价")
    private Integer unitPrice;

    @AutoDocProperty(value = "费用项份数")
    private Double count;

    @AutoDocProperty(value = "费用项总金额")
    private Integer totalAmount;
    
    @AutoDocProperty(value = "平台保障费和全面保障费的折扣,例如：0.7")
    private Double discount;

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

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}
    
}
