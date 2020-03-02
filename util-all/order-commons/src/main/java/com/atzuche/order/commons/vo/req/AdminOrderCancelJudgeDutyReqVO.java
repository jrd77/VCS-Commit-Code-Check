package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * 管理后台取消订单责任判定请求参数
 *
 *
 * @author pengcheng.fu
 * @date 2020/2/25 15:04
 */
public class AdminOrderCancelJudgeDutyReqVO implements Serializable {

    private static final long serialVersionUID = -973533239064189543L;

    @AutoDocProperty(value = "订单号", required = true)
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "责任方:1,租客责任 2,车主责任 6,双方无责、平台承担保险", required = true)
    @NotBlank(message = "责任方不能为空")
    private String wrongdoer;

    @AutoDocProperty(value = "操作人", required = true)
    @NotBlank(message = "操作人不能为空")
    private String operatorName;


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getWrongdoer() {
        return wrongdoer;
    }

    public void setWrongdoer(String wrongdoer) {
        this.wrongdoer = wrongdoer;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    @Override
    public String toString() {
        return "AdminOrderCancelJudgeDutyReqVO{" +
                "orderNo='" + orderNo + '\'' +
                ", wrongdoer='" + wrongdoer + '\'' +
                ", operatorName='" + operatorName + '\'' +
                '}';
    }
}
