package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * 获取订单责任判定列表
 *
 * @author pengcheng.fu
 * @date 2020/3/12 11:50
 */
public class AdminOrderJudgeDutyReqVO implements Serializable {

    private static final long serialVersionUID = -1884233104591106142L;


    @AutoDocProperty(value = "订单号", required = true)
    @NotBlank(message = "订单号不能为空")
    private String orderNo;


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
