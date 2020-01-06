package com.atzuche.order.admin.vo.request;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * Created by qincai.lin on 2019/12/30.
 */
public class OrderGetReturnCarRemarkRequestVO {

    @AutoDocProperty(value = "订单号")
    private String orderNo;

    @AutoDocProperty(value = "取还车备注内容")
    private String remarkContent;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getRemarkContent() {
        return remarkContent;
    }

    public void setRemarkContent(String remarkContent) {
        this.remarkContent = remarkContent;
    }



}
