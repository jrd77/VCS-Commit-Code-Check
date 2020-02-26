package com.atzuche.order.commons.vo.res.order;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.io.Serializable;

/**
 * 租车费用小计信息
 *
 * @author pengcheng.fu
 * @date 2020/1/11 15:16
 */
public class TotalCostVO implements Serializable {

    private static final long serialVersionUID = 8629850469878842670L;

    @AutoDocProperty(value = "费用小计(抵扣前),小计=订单总租金+订单取还车服务费(包含超运能溢价)+订单基础保障费+订单手续费+订单超级补充全险（若购买）+附加驾驶员保障费,如:2100")
    private Integer totalFee;



    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

}
