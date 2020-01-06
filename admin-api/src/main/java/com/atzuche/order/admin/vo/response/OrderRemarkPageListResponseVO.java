package com.atzuche.order.admin.vo.response;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.util.List;

/**
 * Created by qincai.lin on 2020/1/2.
 */
public class OrderRemarkPageListResponseVO {


    @AutoDocProperty(value = "订单备注列表")
    private List<OrderRemarkListResponseVO> orderRemarkList;

    public List<OrderRemarkListResponseVO> getOrderRemarkList() {
        return orderRemarkList;
    }

    public void setOrderRemarkList(List<OrderRemarkListResponseVO> orderRemarkList) {
        this.orderRemarkList = orderRemarkList;
    }


}
