package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * 车主同意取消订单延时退款操作
 *
 * @author pengcheng.fu
 * @date 2020/3/16 16:11
 */
public class CancelOrderDelayRefundReqVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = 868476159069119771L;

    @AutoDocProperty(value = "订单号", required = true)
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "是否收取违约金:0,否 1,是", required = true)
    @NotBlank(message = "是否收取违约金不能为空")
    private String takePenalty;


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTakePenalty() {
        return takePenalty;
    }

    public void setTakePenalty(String takePenalty) {
        this.takePenalty = takePenalty;
    }
}
