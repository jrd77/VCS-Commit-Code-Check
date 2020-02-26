package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 租客取车请求参数
 *
 * @author pengcheng.fu
 * @date 2020/2/12 11:00
 */
public class GetCarReqVO extends BaseVO {

    @AutoDocProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "操作人")
    private String operatorName;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
}
