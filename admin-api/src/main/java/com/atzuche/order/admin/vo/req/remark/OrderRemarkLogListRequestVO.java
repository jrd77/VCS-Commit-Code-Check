package com.atzuche.order.admin.vo.req.remark;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * Created by qincai.lin on 2019/12/30.
 */
public class OrderRemarkLogListRequestVO {

    @AutoDocProperty(value = "订单号")
    private String orderNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }


}
